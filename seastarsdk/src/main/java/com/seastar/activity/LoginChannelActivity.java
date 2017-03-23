package com.seastar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.android.gms.games.Player;
import com.seastar.R;
import com.seastar.config.Config;
import com.seastar.helper.AuthHelper;
import com.seastar.helper.FacebookHelper;
import com.seastar.helper.GoogleApiClientHelper;
import com.seastar.listener.OnActionFinishListener;
import com.seastar.utils.ActivityMgr;
import com.seastar.utils.Utility;


public class LoginChannelActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_channel);

        Button btnFb = (Button) findViewById(R.id.login_channel_facebook);
        btnFb.setOnClickListener(this);
        Button btnGoogle = (Button) findViewById(R.id.login_channel_google);
        btnGoogle.setOnClickListener(this);
        Button btnGuest = (Button) findViewById(R.id.login_channel_guest);
        btnGuest.setOnClickListener(this);
        Button btnSeastar = (Button) findViewById(R.id.login_channel_seastar);
        btnSeastar.setOnClickListener(this);

        TextView Ghosttxt = (TextView) findViewById(R.id.login_channel_guest_txt);
        Ghosttxt.setOnClickListener(this);
        TextView Seastartxt = (TextView) findViewById(R.id.login_channel_seastar_txt);
        Seastartxt.setOnClickListener(this);
        TextView Facebooktxt = (TextView) findViewById(R.id.login_channel_facebook_txt);
        Facebooktxt.setOnClickListener(this);
        TextView Googletxt = (TextView) findViewById(R.id.login_channel_google_txt);
        Googletxt.setOnClickListener(this);

        Button back = (Button) findViewById(R.id.head_landscape_back);
        if (back != null) {
            if (ActivityMgr.getInstance().showLoginChannelBack())
                back.setVisibility(View.VISIBLE);
            else
                back.setVisibility(View.INVISIBLE);
            back.setOnClickListener(this);
        }
        back = (Button) findViewById(R.id.head_back);
        if (back != null) {
            back.setOnClickListener(this);
            if (ActivityMgr.getInstance().showLoginChannelBack())
                back.setVisibility(View.VISIBLE);
            else
                back.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (ActivityMgr.getInstance().showLoginChannelBack()) {
            ActivityMgr.getInstance().navigateToAccountListAndFinish(this);
        } else {
            // 关闭
            onLoginResult(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookHelper.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.login_channel_facebook || v.getId() == R.id.login_channel_facebook_txt) {
            FacebookHelper.getInstance().login(this, new OnActionFinishListener() {
                @Override
                public void onFinished(boolean success) {
                    if (success) {
                        AccessToken accessToken = FacebookHelper.getInstance().getAccessToken();

                        showProgressDialog();
                        AuthHelper.getInstance().doLoginAndRegistAndLogin(accessToken.getUserId(), accessToken.getToken(), Config.TYPE_FACEBOOK, new OnActionFinishListener() {
                            @Override
                            public void onFinished(boolean result) {
                                hideProgressDialog();

                                onLoginResult(result);
                            }
                        });
                    } else {
                        onLoginResult(false);
                    }
                }
            });
        } else if (v.getId() == R.id.login_channel_google || v.getId() == R.id.login_channel_google_txt) {
            if (GoogleApiClientHelper.getInstance().isConnected()) {
                Player player = GoogleApiClientHelper.getInstance().getPlayer();

                showProgressDialog();
                AuthHelper.getInstance().doLoginAndRegistAndLogin(player.getPlayerId(), player.getPlayerId(), Config.TYPE_GOOGLE, new OnActionFinishListener() {
                    @Override
                    public void onFinished(boolean result) {
                        hideProgressDialog();

                        onLoginResult(result);
                    }
                });
            } else {
                GoogleApiClientHelper.getInstance().setConnectActionListener(new OnActionFinishListener() {
                    @Override
                    public void onFinished(boolean success) {
                        if (success) {
                            Player player = GoogleApiClientHelper.getInstance().getPlayer();

                            showProgressDialog();
                            AuthHelper.getInstance().doLoginAndRegistAndLogin(player.getPlayerId(), player.getPlayerId(), Config.TYPE_GOOGLE, new OnActionFinishListener() {
                                @Override
                                public void onFinished(boolean result) {
                                    hideProgressDialog();

                                    onLoginResult(result);
                                }
                            });
                        } else {
                            onLoginResult(false);
                        }
                    }
                });
                GoogleApiClientHelper.getInstance().connect();
            }
        } else if (v.getId() == R.id.login_channel_guest || v.getId() == R.id.login_channel_guest_txt) {
            showProgressDialog();
            AuthHelper.getInstance().doLoginAndRegistAndLogin(Utility.getDeviceId(this), Utility.getDeviceId(this), Config.TYPE_GUEST, new OnActionFinishListener() {
                @Override
                public void onFinished(boolean result) {
                    hideProgressDialog();

                    onLoginResult(result);
                }
            });
        } else if (v.getId() == R.id.login_channel_seastar || v.getId() == R.id.login_channel_seastar_txt) {
            ActivityMgr.getInstance().navigateToSeastarLoginAndFinish(this);
        } else if (v.getId() == R.id.head_back || v.getId() == R.id.head_landscape_back) {
            ActivityMgr.getInstance().navigateToAccountListAndFinish(this);
        }
    }
}


