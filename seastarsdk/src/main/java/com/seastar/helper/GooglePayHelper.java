package com.seastar.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.seastar.listener.NetworkListener;
import com.seastar.listener.OnPurchaseFinishListener;
import com.seastar.model.AppModel;
import com.seastar.model.GooglePurchaseModel;
import com.seastar.model.UserModel;
import com.seastar.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by osx on 17/2/28.
 */

public class GooglePayHelper {
    private static GooglePayHelper instance = new GooglePayHelper();

    private BillingProcessor billingProcessor;
    private AppModel appModel;

    private GooglePurchaseModel googlePurchaseModel = new GooglePurchaseModel();

    private NetworkHelper networkHelper = new NetworkHelper();
    private ObjectMapper objectMapper = new ObjectMapper();

    private OnPurchaseFinishListener onPurchaseFinishListener = null;

    private Activity curActivity;

    public static GooglePayHelper getInstance() {
        return instance;
    }

    public void init(final Context context) {
        appModel = new AppModel(context);

        if (BillingProcessor.isIabServiceAvailable(context)) {
            billingProcessor = new BillingProcessor(context, null, new BillingProcessor.IBillingHandler() {

                @Override
                public void onBillingInitialized() {
                    /*
                     * Called when BillingProcessor was initialized and it's ready to purchase
                     */
                    Log.d("Seastar", "billing Initialized");
                    ArrayList<GooglePurchaseModel> models = GooglePurchaseModel.getAll(context);
                    for (final GooglePurchaseModel model: models) {
                        postPurchaseToServer(model, new NetworkListener() {
                            @Override
                            public void onFailure() {}

                            @Override
                            public void onSuccess(int code, String body) {}
                        });
                    }

                    billingProcessor.loadOwnedPurchasesFromGoogle();
                    for(String sku : billingProcessor.listOwnedProducts()) {
                        billingProcessor.consumePurchase(sku);
                        Log.d("Seastar", "consume sku: " + sku);
                    }
                }

                @Override
                public void onProductPurchased(String productId, TransactionDetails details) {
                    /*
                     * Called when requested PRODUCT ID was successfully purchased
                     */
                    billingProcessor.consumePurchase(productId);
                    Log.d("Seastar", "responseData: " + details.purchaseInfo.responseData + "\nSignature:" + details.purchaseInfo.signature + "\nState:" + details.purchaseInfo.purchaseData.purchaseState);

                    googlePurchaseModel.originalJson = Base64.encodeToString(details.purchaseInfo.responseData.getBytes(), Base64.NO_WRAP);
                    googlePurchaseModel.signature = details.purchaseInfo.signature;

                    final ProgressDialog pd = Utility.showProgress(curActivity);
                    postPurchaseToServer(googlePurchaseModel, new NetworkListener() {
                        @Override
                        public void onFailure() {
                            Utility.hideProgress(curActivity, pd);

                            googlePurchaseModel.save(context);
                            clearPurchaseModel();

                            if (onPurchaseFinishListener != null)
                                onPurchaseFinishListener.onFinished(false, "");
                        }

                        @Override
                        public void onSuccess(int code, String body) {
                            Utility.hideProgress(curActivity, pd);

                            clearPurchaseModel();
                            if (code == 200) {
                                String order = "";
                                try {
                                    JsonNode node = objectMapper.readTree(body);
                                    order = node.get("order").asText();
                                    String sku = node.get("sku").asText();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (onPurchaseFinishListener != null)
                                    onPurchaseFinishListener.onFinished(true, order);
                            } else {
                                if (onPurchaseFinishListener != null)
                                    onPurchaseFinishListener.onFinished(false, "");
                            }
                        }
                    });
                }

                @Override
                public void onPurchaseHistoryRestored() {
                    Log.d("Seastar", "purchase history");
                    /*
                     * Called when purchase history was restored and the list of all owned PRODUCT ID's
                     * was loaded from Google Play
                     */

                }

                @Override
                public void onBillingError(int errorCode, Throwable error) {
                    /*
                     * Called when some error occurred. See Constants class for more details
                     *
                     * Note - this includes handling the case where the user canceled the buy dialog:
                     * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
                     */
                    Log.d("Seastar", "Billing Error: " + errorCode + error);
                    clearPurchaseModel();
                    if (onPurchaseFinishListener != null)
                        onPurchaseFinishListener.onFinished(false, "");
                }
            });
        }
    }

    public void release() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor != null) {
            billingProcessor.handleActivityResult(requestCode, resultCode, data);
        }
    }

    public void doPurchase(Activity activity, String productId, String roleId, String serverId, String extra, final OnPurchaseFinishListener listener) {
        UserModel userModel = UserModel.getCurrentUser(activity);
        if (userModel == null || billingProcessor == null || !googlePurchaseModel.developerPayload.isEmpty()) {
            return;
        }
        curActivity = activity;
        onPurchaseFinishListener = listener;

        googlePurchaseModel.userId = userModel.getUserId();
        googlePurchaseModel.token = userModel.getToken();
        googlePurchaseModel.roleId = roleId;
        googlePurchaseModel.serverId = serverId;
        googlePurchaseModel.sku = productId;
        googlePurchaseModel.extra = extra;//Base64.encodeToString(extra.getBytes(), Base64.NO_WRAP);
        googlePurchaseModel.developerPayload = UUID.randomUUID().toString().replace("-", "");

        SkuDetails details = billingProcessor.getPurchaseListingDetails(googlePurchaseModel.sku);
        if (details == null) {
            details = billingProcessor.getSubscriptionListingDetails(googlePurchaseModel.sku);
            if (details != null)
                googlePurchaseModel.itemType = Constants.PRODUCT_TYPE_SUBSCRIPTION;
        } else {
            googlePurchaseModel.itemType = Constants.PRODUCT_TYPE_MANAGED;
        }

        if (details != null) {
            Log.d("Seastar", "begin purchase " + details.productId);

            googlePurchaseModel.itemType = Constants.PRODUCT_TYPE_MANAGED;
            googlePurchaseModel.price = details.priceValue;
            googlePurchaseModel.currency = details.currency;

            if (googlePurchaseModel.itemType.equals(Constants.PRODUCT_TYPE_MANAGED))
                billingProcessor.purchase(activity, googlePurchaseModel.sku, googlePurchaseModel.developerPayload);
            else
                billingProcessor.subscribe(activity, googlePurchaseModel.sku, googlePurchaseModel.developerPayload);
        } else {
            Log.d("Seastar", "no sku");
            clearPurchaseModel();
            if (listener != null)
                listener.onFinished(false, "");
        }
    }

    public SkuDetails getPurchaseSkuDetails(String sku) {
        if (billingProcessor == null)
            return null;

        return billingProcessor.getPurchaseListingDetails(sku);
    }

    public SkuDetails getSubscriptionSkuDetails(String sku) {
        if (billingProcessor == null)
            return null;

        return billingProcessor.getSubscriptionListingDetails(sku);
    }

    private void postPurchaseToServer(GooglePurchaseModel model, NetworkListener listener) {
        String body = "";
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("googleOriginalJson", model.originalJson);
            root.put("googleSignature", model.signature);
            root.put("gameRoleId", model.roleId);
            root.put("serverId", model.serverId);
            root.put("cparam", model.extra);
            root.put("price", model.price + "");
            root.put("currencyCode", model.currency);
            body = objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + googlePurchaseModel.token);
        networkHelper.post(appModel.getServerUrl() + "/api/pay/google", headers, body, listener);
    }

    private void clearPurchaseModel() {
        googlePurchaseModel.developerPayload = "";
    }

}
