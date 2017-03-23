package com.seastar.helper;

import android.util.Log;

import com.seastar.listener.NetworkListener;
import com.seastar.listener.OnActionTwoFinishListener;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by osx on 16/11/7.
 */

public class NetworkHelper {
    private final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = null;

    public NetworkHelper() {
        client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public void post(String url, Map<String, String> headers, String body, final NetworkListener callback) {
        Log.d("Seastar", "url: " + url + " body: " + body);

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        builder.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, body));

        Request request = builder.build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String body = arg1.body().string();
                Log.d("Seastar", "code: " + arg1.code() + ", body: " + body);
                callback.onSuccess(arg1.code(), body);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.d("Seastar", "exception: " + arg1.getMessage());
                callback.onFailure();
            }
        });
    }

    public void get(String url, Map<String, String> headers, final NetworkListener callback) {
        Log.d("Seastar", "url: " + url);

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        Request request = builder.build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String body = arg1.body().string();
                Log.d("Seastar", "code: " + arg1.code() + ", body: " + body);
                callback.onSuccess(arg1.code(), body);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.d("Seastar", "exception: " + arg1.getMessage());
                callback.onFailure();
            }
        });
    }

    public void put(String url, Map<String, String> headers, String body, final NetworkListener callback) {
        Log.d("Seastar", "url: " + url);

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        builder.put(RequestBody.create(MEDIA_TYPE_MARKDOWN, body));

        Request request = builder.build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String body = arg1.body().string();
                Log.d("Seastar", "code: " + arg1.code() + ", body: " + body);
                callback.onSuccess(arg1.code(), body);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.d("Seastar", "exception: " + arg1.getMessage());
                callback.onFailure();
            }
        });
    }
}
