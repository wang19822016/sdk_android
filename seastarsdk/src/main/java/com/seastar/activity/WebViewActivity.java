package com.seastar.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.seastar.R;
import com.seastar.config.Config;
import com.seastar.model.AppModel;
import com.seastar.model.UserModel;
import com.seastar.utils.Utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by osx on 16/12/19.
 */

public class WebViewActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        String productId = getIntent().getStringExtra("productId");
        String roleId = getIntent().getStringExtra("roleId");
        String serverId = getIntent().getStringExtra("serverId");
        String extra = getIntent().getStringExtra("extra");

        webView = (WebView) findViewById(R.id.webview);

        // 支持js
        webView.getSettings().setJavaScriptEnabled(true);
        // 网页内操作window.jsObj
        webView.addJavascriptInterface(this, "jsObj");
        // 支持缓存
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        // 支持手势聚焦
        webView.requestFocusFromTouch();
        // 在应用内使用网页
        webView.setWebViewClient(new WebViewClient() {

            private ProgressDialog dialog = new ProgressDialog(WebViewActivity.this);

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // webview内加载页面
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 加载开始弹出进度框
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Loading...");
                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 加载结束关闭进度框
                dialog.dismiss();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                // js alert能够显示
                AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                builder.setTitle("Alert");
                builder.setMessage(message);
                builder.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                // js confirm能够显示
                AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage(message);
                builder.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }
        });

        UserModel userModel = UserModel.getCurrentUser(this);
        AppModel appModel = new AppModel(this);

        Map<String, String> additionalHttpHeaders = new HashMap<>();
        additionalHttpHeaders.put("Authorization", "Bearer " + userModel.getToken());
        webView.loadUrl(appModel.getServerUrl() + "/api/pay?sku=" + Utility.toUrlEncode(productId) + "&customer=" + Utility.toUrlEncode(roleId) +
                    "&server=" + Utility.toUrlEncode(serverId) + "&extra=" + Utility.toUrlEncode(extra), additionalHttpHeaders);
    }
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         * 这里监控的是物理返回或者设置了该接口的点击事件
         * 当按钮事件为返回时，且WebView可以返回，即触发返回事件
         */
        /*if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); // goBack()表示返回WebView的上一页面
            return true; // 返回true拦截事件的传递
        }
        return false;
    }*/

    @JavascriptInterface
    public void google() {
        setResult(Config.RESULT_CODE_GOOGLE, getIntent());
        finish();
    }

    @JavascriptInterface()
    public void mycard() {
        setResult(Config.RESULT_CODE_MYCARD, getIntent());
        finish();
    }

    // 通过window.jsObj.cancel使用
    @JavascriptInterface
    public void cancel() {
        finish();
    }

    @JavascriptInterface
    public void fail() {
        Log.d("Seastar", "支付失败");
        getIntent().putExtra("order", "");
        getIntent().putExtra("success", false);
        setResult(Config.RESULT_CODE_PAYPAL, getIntent());
        finish();
    }

    @JavascriptInterface
    public void success(final String order) {
        getIntent().putExtra("order", order);
        getIntent().putExtra("success", true);
        setResult(Config.RESULT_CODE_PAYPAL, getIntent());
        finish();
    }

}
