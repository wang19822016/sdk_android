package com.seastar.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seastar.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by osx on 16/11/4.
 */

public class UserModel {
    private ObjectMapper mapper = new ObjectMapper();
    private String token = "";

    public UserModel() {}
    public UserModel(String token) {
        this.token = token;
    }

    public void save(Context context, boolean isCurrentUser) {
        ArrayList<UserModel> models = getAll(context);

        for (UserModel model : models) {
            if (model.getUserId() == getUserId()) {
                models.remove(model);
                break;
            }
        }
        models.add(this);

        saveAll(context, models);

        if (isCurrentUser) {
            saveCurrentUser(context, token);
        }
    }

    public static void clearExpire(Context context) {
        ArrayList<UserModel> models = getAll(context);
        ArrayList<UserModel> notExpire = new ArrayList<>();
        UserModel cur = getCurrentUser(context);

        // 提前7天过期
        long testTime = System.currentTimeMillis() / 1000 + 7 * 24 * 60 * 60;

        for (UserModel model : models) {
            if (testTime <= model.expired()) {
                notExpire.add(model);
            } else {
                if (cur != null) {
                    if (model.getUserId() == cur.getUserId()) {
                        removeCurrentUser(context);
                    }
                }
            }
        }

        if (models.size() != notExpire.size())
            saveAll(context, notExpire);
    }

    public static UserModel get(Context context, long userId) {
        ArrayList<UserModel> models = getAll(context);

        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).getUserId() == userId) return models.get(i);
        }

        return null;
    }

    public static UserModel getCurrentUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("users_info", Context.MODE_PRIVATE);
        String userStr = settings.getString("current", "");

        if (userStr.isEmpty()) {
            return null;
        } else {
            return new UserModel(userStr);
        }
    }

    private static void saveCurrentUser(Context context, String token) {
        SharedPreferences settings = context.getSharedPreferences("users_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("current", token);
        editor.commit();
    }


    public static void removeCurrentUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("users_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("current", "");
        editor.commit();
    }

    // 默认登录帐号在第一个
    public static ArrayList<UserModel> getAll(Context context) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            ArrayList<UserModel> models = new ArrayList<>();

            SharedPreferences settings = context.getSharedPreferences("users_info", Context.MODE_PRIVATE);
            JsonNode root = objectMapper.readTree(settings.getString("users_list", "[]"));
            if (root.isArray()) {
                for (JsonNode node : root) {
                    models.add(new UserModel(node.asText()));
                }
            }

            return models;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static void remove(Context context, long userId) {
        ArrayList<UserModel> models = getAll(context);

        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).getUserId() == userId) {
                models.remove(i);
                break;
            }
        }

        saveAll(context, models);
    }

    private static void saveAll(Context context, ArrayList<UserModel> list) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            ArrayList<String> myList = new ArrayList<>();
            for (UserModel model : list) {
                myList.add(model.getToken());
            }

            SharedPreferences settings = context.getSharedPreferences("users_info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("users_list", mapper.writeValueAsString(myList));
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }

    public long getUserId() {
        String[] strArray = token.split("\\.");
        if (strArray.length == 3) {
            try {
                JsonNode root = mapper.readTree(Utility.base64UrlDecode(strArray[1]));
                return root.path("userId").asLong();
            } catch (Exception e) {

            }
        }

        return 0;
    }

    private long getUserId(String inputToken) {
        String[] strArray = inputToken.split("\\.");
        if (strArray.length == 3) {
            try {
                JsonNode root = mapper.readTree(Utility.base64UrlDecode(strArray[1]));
                return root.path("userId").asLong();
            } catch (Exception e) {

            }
        }

        return 0;
    }

    public String getUsername() {
        String[] strArray = token.split("\\.");
        if (strArray.length == 3) {
            try {
                JsonNode root = mapper.readTree(Utility.base64UrlDecode(strArray[1]));
                return root.path("username").asText();
            } catch (Exception e) {

            }
        }

        return "";
    }

    public int getAppId() {
        String[] strArray = token.split("\\.");
        if (strArray.length == 3) {
            try {
                JsonNode root = mapper.readTree(Utility.base64UrlDecode(strArray[1]));
                return root.path("appId").asInt();
            } catch (Exception e) {

            }
        }

        return 0;
    }

    public int getPayType() {
        String[] strArray = token.split("\\.");
        if (strArray.length == 3) {
            try {
                JsonNode root = mapper.readTree(Utility.base64UrlDecode(strArray[1]));
                return root.path("payType").asInt();
            } catch (Exception e) {

            }
        }

        return 0;
    }

    public int getLoginType() {
        String[] strArray = token.split("\\.");
        if (strArray.length == 3) {
            try {
                JsonNode root = mapper.readTree(Utility.base64UrlDecode(strArray[1]));
                return root.path("loginType").asInt();
            } catch (Exception e) {

            }
        }

        return 0;
    }

    public long expired() {
        String[] strArray = token.split("\\.");
        if (strArray.length == 3) {
            try {
                JsonNode root = mapper.readTree(Utility.base64UrlDecode(strArray[1]));
                return root.path("exp").asLong();
            } catch (Exception e) {

            }
        }

        return 0;
    }
}
