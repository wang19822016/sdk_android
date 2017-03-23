package com.seastar.helper;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.seastar.listener.NetworkListener;
import com.seastar.listener.OnActionFinishListener;
import com.seastar.model.AppModel;
import com.seastar.model.UserModel;
import com.seastar.utils.Utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by osx on 17/2/28.
 */

public class AuthHelper {
    private Context context;
    private static AuthHelper instance = new AuthHelper();
    private ObjectMapper mapper = new ObjectMapper();
    private NetworkHelper networkHelper = new NetworkHelper();
    private AppModel appModel;

    public static AuthHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        appModel = new AppModel(context);
        this.context = context;
    }

    public void doRegist(final String username, final String password, String email, final int type, final OnActionFinishListener listener) {

        String sign = Utility.md5encode(appModel.getAppId() + "" + type + email + appModel.getAppKey());

        ObjectNode root = mapper.createObjectNode();
        root.put("appId", appModel.getAppId());
        root.put("email", email);
        root.put("type", type);
        root.put("sign", sign);

        String postBody = "";
        try {
            postBody = mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Utility.b64encode(username + ":" + password));

        networkHelper.post(appModel.getServerUrl() + "/api/user", headers, postBody, new NetworkListener() {
            @Override
            public void onFailure() {
                listener.onFinished(false);
            }

            @Override
            public void onSuccess(int code, String body) {
                if (code == 201) {
                    listener.onFinished(true);
                } else {
                    listener.onFinished(false);
                }
            }
        });
    }

    public void doLogin(String username, String password, int type, final OnActionFinishListener listener) {
        String sign = Utility.md5encode(appModel.getAppId() + "" + type + appModel.getAppKey());
        ObjectNode root = mapper.createObjectNode();
        root.put("appId", appModel.getAppId());
        root.put("type", type);
        root.put("sign", sign);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Utility.b64encode(username + ":" + password));

        String postBody = "";
        try {
            postBody = mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        networkHelper.post(appModel.getServerUrl() + "/api/user/token", headers, postBody, new NetworkListener() {
            @Override
            public void onFailure() {
                listener.onFinished(false);
            }

            @Override
            public void onSuccess(int code, String body) {
                if (code == 200) {
                    String access_token = "";
                    try {
                        JsonNode root = mapper.readTree(body);
                        access_token = root.path("access_token").asText();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    UserModel userModel = new UserModel(access_token);
                    userModel.save(context, true);
                    listener.onFinished(true);
                } else {
                    listener.onFinished(false);
                }
            }
        });
    }

    public void doLoginAndRegistAndLogin(final String username, final String password, final int type, final OnActionFinishListener listener) {
        String sign = Utility.md5encode(appModel.getAppId() + "" + type + appModel.getAppKey());

        ObjectNode root = mapper.createObjectNode();
        root.put("appId", appModel.getAppId());
        root.put("type", type);
        root.put("sign", sign);

        String postBody = "";
        try {
            postBody = mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Utility.b64encode(username + ":" + password));

        networkHelper.post(appModel.getServerUrl() + "/api/user/token", headers, postBody, new NetworkListener() {
            @Override
            public void onFailure() {
                listener.onFinished(false);
            }

            @Override
            public void onSuccess(int code, String body) {
                if (code == 200) {
                    String access_token = "";
                    try {
                        JsonNode root = mapper.readTree(body);
                        access_token = root.path("access_token").asText();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    UserModel userModel = new UserModel(access_token);
                    userModel.save(context, true);
                    listener.onFinished(true);
                } else if (code == 404) {
                    // 帐号不存在时，注册帐号
                    doRegist(username, password, "", type, new OnActionFinishListener() {
                        @Override
                        public void onFinished(boolean success) {
                            if (success) {
                                // 注册成功后尝试登录
                                doLogin(username, password, type, listener);
                            } else {
                                listener.onFinished(false);
                            }
                        }
                    });
                } else {
                    listener.onFinished(false);
                }
            }
        });
    }

    public void hasEmail(final OnActionFinishListener listener) {
        UserModel userModel = UserModel.getCurrentUser(context);
        if (userModel == null) return;

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userModel.getToken());
        networkHelper.get(appModel.getServerUrl() + "/api/user", headers, new NetworkListener() {
            @Override
            public void onFailure() {
                listener.onFinished(true); // 返回true，防止弹出窗口
            }

            @Override
            public void onSuccess(int code, String body) {
                if (code == 200) {
                    String email = "";
                    try {
                        JsonNode root = mapper.readTree(body);
                        if (root.has("email")) {
                            email = root.get("email").asText();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!email.isEmpty()) {
                        listener.onFinished(true);
                    } else {
                        listener.onFinished(false);
                    }
                } else {
                    listener.onFinished(true);
                }
            }
        });
    }

    public void updateEmail(String email) {
        UserModel userModel = UserModel.getCurrentUser(context);
        if (userModel == null) return;

        String postBody = "";
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("email", email);
            postBody = mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userModel.getToken());
        networkHelper.put(appModel.getServerUrl() + "/api/user", headers, postBody, new NetworkListener() {
            @Override
            public void onFailure() {
            }

            @Override
            public void onSuccess(int code, String body) {
            }
        });
    }

    public void doFindPwd(String username) {
        String sign = Utility.md5encode(appModel.getAppId() + username + appModel.getAppKey());
        String url = appModel.getServerUrl() + "/api/user/pwd?username=" + username + "&appId=" + appModel.getAppId() + "&sign=" + sign;

        networkHelper.get(url, new HashMap<String, String>(), new NetworkListener() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccess(int code, String body) {

            }
        });
    }

}
