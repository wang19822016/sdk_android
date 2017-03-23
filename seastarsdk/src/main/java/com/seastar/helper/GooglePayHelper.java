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

    private BillingProcessor bp;
    private AppModel appModel;

    private GooglePurchaseModel googlePurchaseModel = null;

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
            bp = new BillingProcessor(context, null, new BillingProcessor.IBillingHandler() {
                @Override
                public void onProductPurchased(String productId, TransactionDetails details) {
                    /*
                     * Called when requested PRODUCT ID was successfully purchased
                     */
                    googlePurchaseModel.originalJson = Base64.encodeToString(details.purchaseInfo.responseData.getBytes(), Base64.NO_WRAP);
                    googlePurchaseModel.signature = details.purchaseInfo.signature;
                    Log.d("Seastar", googlePurchaseModel.sku + " " + googlePurchaseModel.originalJson);

                    final ProgressDialog pd = Utility.showProgress(curActivity);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + googlePurchaseModel.token);
                    networkHelper.post(appModel.getServerUrl() + "/api/pay/google", headers, getPostBody(googlePurchaseModel), new NetworkListener() {
                        @Override
                        public void onFailure() {
                            Utility.hideProgress(curActivity, pd);

                            googlePurchaseModel.save(context);
                            googlePurchaseModel = null;

                            onPurchaseFinishListener.onFinished(false, "");
                        }

                        @Override
                        public void onSuccess(int code, String body) {
                            Utility.hideProgress(curActivity, pd);

                            googlePurchaseModel = null;
                            if (code == 200) {
                                String order = "";
                                try {
                                    JsonNode node = objectMapper.readTree(body);
                                    order = node.get("order").asText();
                                    String sku = node.get("sku").asText();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                onPurchaseFinishListener.onFinished(true, order);
                            } else {
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
                    for(String sku : bp.listOwnedProducts()) {
                        bp.consumePurchase(sku);
                        Log.d("Seastar", "sku: " + sku);
                    }
                }

                @Override
                public void onBillingError(int errorCode, Throwable error) {
                    /*
                     * Called when some error occurred. See Constants class for more details
                     *
                     * Note - this includes handling the case where the user canceled the buy dialog:
                     * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
                     */
                    googlePurchaseModel = null;
                    onPurchaseFinishListener.onFinished(false, "");
                }

                @Override
                public void onBillingInitialized() {
                    /*
                     * Called when BillingProcessor was initialized and it's ready to purchase
                     */
                    Log.d("Seastar", "load owned purchases");
                    ArrayList<GooglePurchaseModel> models = GooglePurchaseModel.getAll(context);
                    for (final GooglePurchaseModel model: models) {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + googlePurchaseModel.token);
                        networkHelper.post(appModel.getServerUrl() + "/api/pay/google", headers, getPostBody(model), new NetworkListener() {
                            @Override
                            public void onFailure() {

                            }

                            @Override
                            public void onSuccess(int code, String body) {
                                model.remove(context);
                            }
                        });
                    }

                    bp.loadOwnedPurchasesFromGoogle();
                }
            });
        }
    }

    public void release() {
        if (bp != null) {
            bp.release();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (bp != null) {
            bp.handleActivityResult(requestCode, resultCode, data);
        }
    }

    public void doPurchase(Activity activity, String productId, String roleId, String serverId, String extra, final OnPurchaseFinishListener listener) {
        UserModel userModel = UserModel.getCurrentUser(activity);
        if (userModel == null || bp == null || googlePurchaseModel != null) {
            return;
        }
        curActivity = activity;
        onPurchaseFinishListener = listener;

        googlePurchaseModel = new GooglePurchaseModel();
        googlePurchaseModel.userId = userModel.getUserId();
        googlePurchaseModel.token = userModel.getToken();
        googlePurchaseModel.roleId = roleId;
        googlePurchaseModel.serverId = serverId;
        googlePurchaseModel.sku = productId;
        googlePurchaseModel.extra = extra;//Base64.encodeToString(extra.getBytes(), Base64.NO_WRAP);
        googlePurchaseModel.developerPayload = UUID.randomUUID().toString().replace("-", "");

        SkuDetails details = bp.getPurchaseListingDetails(googlePurchaseModel.sku);
        if (details == null) {
            details = bp.getSubscriptionListingDetails(googlePurchaseModel.sku);
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
                bp.purchase(activity, googlePurchaseModel.sku, googlePurchaseModel.developerPayload);
            else
                bp.subscribe(activity, googlePurchaseModel.sku, googlePurchaseModel.developerPayload);
        } else {
            Log.d("Seastar", "no sku");
            googlePurchaseModel = null;
            if (listener != null)
                listener.onFinished(false, "");
        }
    }

    public SkuDetails getPurchaseSkuDetails(String sku) {
        if (bp == null)
            return null;

        return bp.getPurchaseListingDetails(sku);
    }

    public SkuDetails getSubscriptionSkuDetails(String sku) {
        if (bp == null)
            return null;

        return bp.getSubscriptionListingDetails(sku);
    }

    private String getPostBody(GooglePurchaseModel model) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("googleOriginalJson", model.originalJson);
            root.put("googleSignature", model.signature);
            root.put("gameRoleId", model.roleId);
            root.put("serverId", model.serverId);
            root.put("cparam", model.extra);
            root.put("price", model.price + "");
            root.put("currencyCode", model.currency);
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        return "";
    }

}
