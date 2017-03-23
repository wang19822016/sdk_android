package com.seastar.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seastar.R;
import com.seastar.config.Config;
import com.seastar.helper.AuthHelper;
import com.seastar.listener.OnActionFinishListener;
import com.seastar.utils.ActivityMgr;

public class SeastarRegistActivity extends BaseActivity implements View.OnClickListener {

    private EditText userName = null;
    private EditText passWord = null;
    private EditText email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seastar_regist);

        userName = (EditText) findViewById(R.id.regist_username);
        passWord = (EditText) findViewById(R.id.regist_password);
        email = (EditText) findViewById(R.id.regist_email);
        Button btnRegister = (Button) findViewById(R.id.regist_button);
        btnRegister.setOnClickListener(this);

        Button back = (Button) findViewById(R.id.head_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        ActivityMgr.getInstance().navigateToSeastarLoginAndFinish(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.regist_button) {
            final String name = userName.getText().toString();
            final String pwd = passWord.getText().toString();
            String em = email.getText().toString();

            // 检查输入是否符合要求
            if (name.isEmpty() || !name.matches("[a-zA-Z][a-zA-Z0-9]{5,60}")) {
                Toast.makeText(this, R.string.tip_username_err, Toast.LENGTH_SHORT).show();
                return;
            }

            if (pwd.isEmpty() || !pwd.matches("[a-zA-Z0-9]{8,32}")) {
                Toast.makeText(this, R.string.tip_password_err, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!em.isEmpty() && !em.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
                Toast.makeText(this, R.string.tip_email_err, Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressDialog();
            AuthHelper.getInstance().doRegist(name, pwd, em, Config.TYPE_ACCOUNT, new OnActionFinishListener() {
                @Override
                public void onFinished(boolean success) {
                    if (success) {
                        // 尝试登录
                        AuthHelper.getInstance().doLogin(name, pwd, Config.TYPE_ACCOUNT, new OnActionFinishListener() {
                            @Override
                            public void onFinished(boolean success) {
                                hideProgressDialog();
                                onLoginResult(success);
                            }
                        });
                    } else {
                        hideProgressDialog();

                        SeastarRegistActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SeastarRegistActivity.this, R.string.tip_regist_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        } else if (v.getId() == R.id.head_back) {
            ActivityMgr.getInstance().navigateToSeastarLoginAndFinish(this);
        }
    }
}
