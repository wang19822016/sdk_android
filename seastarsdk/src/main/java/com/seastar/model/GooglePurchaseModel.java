package com.seastar.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by osx on 16/11/4.
 */

public class GooglePurchaseModel {

    public long userId = 0;
    public String token = "";
    public String roleId = "";
    public String serverId = "";
    public String extra = "";

    public String itemType = "";
    public String sku = "";
    public Double price = 0.0;
    public String currency = "";

    public String originalJson = "";
    public String signature = "";
    public String developerPayload = "";

    public void save(Context context) {
        ObjectMapper objectMapper = new ObjectMapper();

        String purchaseStr = getSPPurchase(context);

        try {
            List<GooglePurchaseModel> models = objectMapper.readValue(purchaseStr, new TypeReference<List<GooglePurchaseModel>>() {});
            for (GooglePurchaseModel model : models) {
                if (model.developerPayload.equals(developerPayload)) {
                    models.remove(model);
                    break;
                }
            }
            models.add(this);

            saveSPPurchase(context, objectMapper.writeValueAsString(models));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove(Context context) {
        ObjectMapper objectMapper = new ObjectMapper();

        String purchaseStr = getSPPurchase(context);

        try {
            List<GooglePurchaseModel> models = objectMapper.readValue(purchaseStr, new TypeReference<List<GooglePurchaseModel>>() {});
            for (GooglePurchaseModel model : models) {
                if (model.developerPayload.equals(developerPayload)) {
                    models.remove(model);
                    break;
                }
            }

            saveSPPurchase(context, objectMapper.writeValueAsString(models));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<GooglePurchaseModel> getAll(Context context) {
        ObjectMapper objectMapper = new ObjectMapper();

        String purchaseStr = getSPPurchase(context);

        try {
            return objectMapper.readValue(purchaseStr, new TypeReference<List<GooglePurchaseModel>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<GooglePurchaseModel>();
    }

    private static String getSPPurchase(Context context) {
        SharedPreferences settings = context.getSharedPreferences("google_purchase", Context.MODE_PRIVATE);
        return settings.getString("googles", "[]");
    }

    private static void saveSPPurchase(Context context, String str) {
        SharedPreferences settings = context.getSharedPreferences("google_purchase", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("googles", str);
        editor.commit();
    }


}
