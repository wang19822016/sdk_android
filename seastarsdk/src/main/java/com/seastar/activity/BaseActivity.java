package com.seastar.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.seastar.listener.ListenerMgr;
import com.seastar.model.UserModel;


/**
 * Created by osx on 17/3/18.
 */

public class BaseActivity extends Activity {
    private ProgressDialog pd = null;
    private Handler handler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading...");

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    pd.show();
                } else if (msg.what == 2) {
                    pd.dismiss();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        pd = null;
        handler = null;
    }

    protected void showProgressDialog() {
        handler.sendEmptyMessage(1);
    }

    protected void hideProgressDialog() {
        handler.sendEmptyMessage(2);
    }

    protected void onLoginResult(boolean result) {
        UserModel model = UserModel.getCurrentUser(this);
        finish();
        if (result) {
            ListenerMgr.getInstance().getLoginFinishListener().onFinished(true, model.getUserId() + "", model.getToken());
        } else {

            ListenerMgr.getInstance().getLoginFinishListener().onFinished(false, "", "");
        }


    }
}
