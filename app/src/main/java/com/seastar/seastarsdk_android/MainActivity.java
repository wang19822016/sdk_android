package com.seastar.seastarsdk_android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.seastar.SeastarSdk;
import com.seastar.activity.FacebookSocialActivity;
import com.seastar.listener.OnActionFinishListener;
import com.seastar.listener.OnLoginFinishListener;
import com.seastar.listener.OnPurchaseFinishListener;
import com.ss.yaomengen.R;

public class MainActivity extends Activity implements View.OnClickListener{

    private EditText etTest;
    private ListPopupWindow lpw;
    private String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SeastarSdk.current.onCreate(this, savedInstanceState);

        Button btnFb = (Button) findViewById(R.id.btn_login);
        btnFb.setOnClickListener(this);
        Button btnrelogin = (Button) findViewById(R.id.btn_change);
        btnrelogin.setOnClickListener(this);
        Button btnlogout = (Button) findViewById(R.id.btn_logout);
        btnlogout.setOnClickListener(this);
        Button btnPay = (Button) findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SeastarSdk.current.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SeastarSdk.current.onDestory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SeastarSdk.current.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SeastarSdk.current.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_login )
        {
            SeastarSdk.current.showFacebookSocialDialog("https://www.baidu.com", "https://www.baidu.com", "https://www.baidu.com", "https://www.baidu.com", "分享", "分享", new OnActionFinishListener() {
                @Override
                public void onFinished(boolean success) {

                }
            });


            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SeastarSdk.current.login(new OnLoginFinishListener() {
                        @Override
                        public void onFinished(final boolean bool, final String userId, String session) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SeastarSdk.current.updateGoogleScoreOnLeaderboard("CgkIhPKr4OwHEAIQcw", 5);
                                    Toast.makeText(MainActivity.this, "login: " + bool + " " + userId, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            }).start();
            */


            //SeastarSdk.current.trackLevelAchieved(1, 100);
            //SeastarSdk.current.trackPurchase(100, "card", "sku1", "USD");
        }
        else if(v.getId() == R.id.btn_change)
        {
            //SeastarSdk.current.showGoogleLeaderboard();
            SeastarSdk.current.switchAccount(new OnLoginFinishListener() {
                @Override
                public void onFinished(final boolean bool, final String userId, String session) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "login: " + bool + " " + userId, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else if (v.getId() == R.id.btn_pay) {
            //SeastarSdk.current.unlockGoogleAchievement("CgkIhPKr4OwHEAIQAQ");
            //SeastarSdk.current.incrementGoogleAchievement("CgkIhPKr4OwHEAIQAQ", 5);

            SeastarSdk.current.purchase("sh.gp.iap100", "role1", "server1", "extra", new OnPurchaseFinishListener() {
                @Override
                public void onFinished(final boolean bool, final String order) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "purchased: " + bool + " " + order, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else if(v.getId() == R.id.btn_logout)
        {
            //SeastarSdk.current.showGoogleAchievement();
            SeastarSdk.current.logout();
            Toast.makeText(this, com.seastar.R.string.logout, Toast.LENGTH_SHORT).show();
        }

    }

    public class CustomDialog extends Dialog {
        public CustomDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.account_list);
        }
    }
}
