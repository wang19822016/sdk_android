package com.seastar.helper;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.seastar.listener.NetworkListener;
import com.seastar.model.AppModel;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by osx on 17/4/6.
 */

public class BossHelper {
    private static BossHelper instance = new BossHelper();
    private NetworkHelper networkHelper = new NetworkHelper();

    public String appsFlyerUID = "1446034686662-8619927357095541234";

    private ObjectMapper mapper = new ObjectMapper();
    private AppModel appModel;
    private Timer timer = new Timer();

    public long userId = 0;
    public String price = "0";

    public static BossHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        appModel = new AppModel(context);
    }

    public void postReigst() {
        ObjectNode root = mapper.createObjectNode();
        root.put("appId", appModel.getAppId());
        root.put("api", "user/register");
        root.put("userId", userId);
        root.put("deviceId", appsFlyerUID);
        root.put("clientTime", System.currentTimeMillis() / 1000);

        try {
            String body = mapper.writeValueAsString(root);
            networkHelper.post("http://report.vrseastar.com/user/register", new HashMap<String, String>(), body, new NetworkListener() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess(int code, String body) {
                    Log.d("Seastar", "boss, regist, " + code);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void postLogin() {
        ObjectNode root = mapper.createObjectNode();
        root.put("appId", appModel.getAppId());
        root.put("api", "user/login");
        root.put("userId", userId);
        root.put("clientTime", System.currentTimeMillis() / 1000);

        try {
            String body = mapper.writeValueAsString(root);
            networkHelper.post("http://report.vrseastar.com/user/login", new HashMap<String, String>(), body, new NetworkListener() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess(int code, String body) {
                    Log.d("Seastar", "boss, regist, " + code);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postFee() {
        ObjectNode root = mapper.createObjectNode();
        root.put("appId", appModel.getAppId());
        root.put("api", "user/pay");
        root.put("userId", userId);
        root.put("payMoney", price);
        root.put("clientTime", System.currentTimeMillis() / 1000);

        try {
            String body = mapper.writeValueAsString(root);
            networkHelper.post("http://report.vrseastar.com/user/pay", new HashMap<String, String>(), body, new NetworkListener() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess(int code, String body) {
                    Log.d("Seastar", "boss, regist, " + code);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void activeOnline() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ObjectNode root = mapper.createObjectNode();
                root.put("appId", appModel.getAppId());
                root.put("api", "user/online");
                root.put("userId", userId);
                root.put("clientTime", System.currentTimeMillis() / 1000);

                try {
                    String body = mapper.writeValueAsString(root);
                    networkHelper.post("http://report.vrseastar.com/user/online", new HashMap<String, String>(), body, new NetworkListener() {
                        @Override
                        public void onFailure() {

                        }

                        @Override
                        public void onSuccess(int code, String body) {
                            Log.d("Seastar", "boss, online, " + code);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 20, 3 * 60 * 1000);
    }

    public void deactiveOnline() {
        timer.cancel();
    }
}
