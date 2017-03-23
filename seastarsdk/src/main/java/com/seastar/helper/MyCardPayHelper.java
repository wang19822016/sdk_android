package com.seastar.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.seastar.listener.NetworkListener;
import com.seastar.listener.OnPurchaseFinishListener;
import com.seastar.model.AppModel;
import com.seastar.model.MyCardPurchaseModel;
import com.seastar.model.UserModel;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.com.mycard.paymentsdk.MyCardSDK;
import tw.com.mycard.paymentsdk.baseLib.Config;

/**
 * Created by osx on 17/2/28.
 */

public class MyCardPayHelper {
    private static MyCardPayHelper instance = new MyCardPayHelper();
    private Activity curActivity;
    private OnPurchaseFinishListener curListener = null;
    private NetworkHelper networkHelper = new NetworkHelper();
    private AppModel appModel;
    private ObjectMapper objectMapper = new ObjectMapper();
    private MyCardPurchaseModel myCardPurchaseModel;

    public static MyCardPayHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        appModel = new AppModel(context);
    }

    public void checkLeakPurchase(final Activity activity) {
        List<MyCardPurchaseModel> myCardPurchaseModels = MyCardPurchaseModel.getAll(curActivity);
        for (final MyCardPurchaseModel model : myCardPurchaseModels) {

            String postBody = "";
            try {
                ObjectNode root = objectMapper.createObjectNode();
                root.put("facTradeSeq", model.facTradeSeq);
                postBody = objectMapper.writeValueAsString(root);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }

            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Bearer " + model.token);
            networkHelper.post(appModel.getServerUrl() + "/api/pay/mycard/money", header, postBody, new NetworkListener() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess(int code, String body) {
                    model.remove(activity);
                }
            });
        }
    }

    public void doPurchase(Activity activity, final String productId, String roleId, String serverId, String extra, final OnPurchaseFinishListener listener) {
        UserModel userModel = UserModel.getCurrentUser(activity);
        if (userModel == null) {
            return;
        }

        myCardPurchaseModel = new MyCardPurchaseModel();
        myCardPurchaseModel.userId = userModel.getUserId();
        myCardPurchaseModel.token = userModel.getToken();
        myCardPurchaseModel.customerId = roleId;
        myCardPurchaseModel.serverId = serverId;
        myCardPurchaseModel.extra = extra;//Base64.encodeToString(extra.getBytes(), Base64.NO_WRAP);
        myCardPurchaseModel.itemCode = productId;

        curListener = listener;

        String postBody = "";
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("customerId", roleId);
            root.put("serverId", serverId);
            root.put("itemCode", productId);
            root.put("cparam", extra);
            postBody = objectMapper.writeValueAsString(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final MyCardSDK sdk = new MyCardSDK(curActivity);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + userModel.getToken());
        networkHelper.post(appModel.getServerUrl() + "/api/pay/mycard/authcode", header, postBody, new NetworkListener() {
            @Override
            public void onFailure() {
                listener.onFinished(false, "");
            }

            @Override
            public void onSuccess(int code, String body) {
                if (code == 200) {
                    try {
                        JsonNode node = objectMapper.readTree(body);
                        sdk.StartPayActivityForResult(node.get("sandBoxMode").asBoolean(), node.get("authCode").asText());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    listener.onFinished(false, "");
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.Payment_RequestCode) {
            if (Activity.RESULT_OK == resultCode) {
                try {
                    JSONObject js = new JSONObject(data.getStringExtra(Config.PaySDK_Result));
                    if (js.optString("returnCode").equals("1") && js.optString("payResult").equals("3")) {
                        // 交易成功 验证交易并请款
                        myCardPurchaseModel.facTradeSeq = js.getString("facTradeSeq");

                        ObjectNode root = objectMapper.createObjectNode();
                        root.put("facTradeSeq", myCardPurchaseModel.facTradeSeq);
                        Map<String, String> header = new HashMap<>();
                        header.put("Authorization", "Bearer " + myCardPurchaseModel.token);
                        networkHelper.post(appModel.getServerUrl() + "/api/pay/mycard/money", header, objectMapper.writeValueAsString(root), new NetworkListener() {
                            @Override
                            public void onFailure() {
                                myCardPurchaseModel.save(curActivity);
                                curListener.onFinished(false, "");
                            }

                            @Override
                            public void onSuccess(int code, String body) {
                                if (code == 200) {
                                    try {
                                        JsonNode node = objectMapper.readTree(body);
                                        curListener.onFinished(true, node.get("facTradeSeq").asText());
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    curListener.onFinished(false, "");
                                }
                            }
                        });
                    } else {
                        // 交易失败
                        if (curListener != null)
                            curListener.onFinished(false, "");
                        curListener = null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // 交易取消
                if (curListener != null)
                    curListener.onFinished(false, "");
                curListener = null;
            }
        }
    }
}
