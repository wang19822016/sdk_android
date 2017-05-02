package com.seastar.activity;


import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.seastar.R;
import com.seastar.config.Config;
import com.seastar.helper.AuthHelper;
import com.seastar.listener.OnActionFinishListener;
import com.seastar.model.UserModel;
import com.seastar.utils.ActivityMgr;
import com.seastar.utils.Utility;

import java.util.ArrayList;


public class SeastarLoginActivity extends BaseActivity implements View.OnClickListener {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seastar_login);


        Button btnLogin = (Button) findViewById(R.id.seastar_login_button);
        btnLogin.setOnClickListener(this);

        TextView btnFindPwd = (TextView) findViewById(R.id.seastar_login_findpwd);
        btnFindPwd.setOnClickListener(this);
        btnFindPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线

        TextView btnReg = (TextView) findViewById(R.id.seastar_login_regist);
        btnReg.setOnClickListener(this);
        btnReg.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线

        Button back = (Button) findViewById(R.id.head_back);
        back.setOnClickListener(this);

        passWord = (EditText) findViewById(R.id.seastar_login_password);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.seastar_login_username);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line);
        ArrayList<UserModel> alluser = UserModel.getAll(this);
        for (UserModel model : alluser) {
            adapter.add(model.getUsername());
        }
        autoCompleteTextView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        ActivityMgr.getInstance().navigateToLoginChannelAndFinish(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.seastar_login_findpwd) {
            ActivityMgr.getInstance().navigateToFindPwdAndFinish(this);
        } else if (v.getId() == R.id.seastar_login_regist) {
            ActivityMgr.getInstance().navigateToSeastarRegistAndFinish(this);
        } else if (v.getId() == R.id.seastar_login_button) {
            final String username = autoCompleteTextView.getText().toString();
            final String password = passWord.getText().toString();

            // 检查输入是否符合要求
            if (username.isEmpty() || !username.matches("[a-zA-Z][a-zA-Z0-9]{5,60}")) {
                Toast.makeText(this, R.string.tip_username_err, Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty() || !password.matches("[a-zA-Z0-9]{8,32}")) {
                Toast.makeText(this, R.string.tip_password_err, Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressDialog();
            AuthHelper.getInstance().doLogin(username, password, Config.TYPE_ACCOUNT, new OnActionFinishListener() {
                @Override
                public void onFinished(boolean result) {
                    hideProgressDialog();
                    onLoginResult(result);
                }
            });

        } else if (v.getId() == R.id.head_back) {
            ActivityMgr.getInstance().navigateToLoginChannelAndFinish(this);
        }
    }
}
