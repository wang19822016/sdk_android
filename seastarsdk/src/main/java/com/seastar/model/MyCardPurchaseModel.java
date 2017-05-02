package com.seastar.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by osx on 16/11/7.
 */

public class MyCardPurchaseModel {
    public long userId = 0;
    public String token = "";
    public String customerId = "";
    public String serverId = "";
    public String extra = "";

    public String itemCode = "";
    public String facTradeSeq = "";

    public void save(Context context) {
        List<MyCardPurchaseModel> models = getAll(context);

        for (MyCardPurchaseModel model : models) {
            if (model.itemCode.equals(itemCode) && model.facTradeSeq.equals(facTradeSeq)) {
                models.remove(model);
                break;
            }
        }
        models.add(this);

        saveAll(context, models);
    }

    public void remove(Context context) {
        List<MyCardPurchaseModel> models = getAll(context);
        for (MyCardPurchaseModel model : models) {
            if (model.itemCode.equals(itemCode) && model.facTradeSeq.equals(facTradeSeq)) {
                models.remove(model);
                break;
            }
        }

        saveAll(context, models);
    }


    public static List<MyCardPurchaseModel> getAll(Context context) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            SharedPreferences settings = context.getSharedPreferences("mycard_purchase", Context.MODE_PRIVATE);
            return objectMapper.readValue(settings.getString("mycards", "[]"), new TypeReference<List<MyCardPurchaseModel>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private static void saveAll(Context context, List<MyCardPurchaseModel> list) {
        SharedPreferences settings = context.getSharedPreferences("mycard_purchase", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            editor.putString("mycards", objectMapper.writeValueAsString(list));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    private static String getSp(Context context) {
        SharedPreferences settings = context.getSharedPreferences("mycard_purchase", Context.MODE_PRIVATE);
        return settings.getString("mycards", "[]");
    }

    private static void saveSp(Context context, String content) {
        SharedPreferences settings = context.getSharedPreferences("mycard_purchase", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mycards", content);
        editor.commit();
    }
}
