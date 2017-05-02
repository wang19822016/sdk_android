package com.seastar.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.seastar.R;
import com.seastar.helper.AuthHelper;
import com.seastar.model.UserModel;
import com.seastar.utils.ActivityMgr;

import java.util.ArrayList;

public class FindPwdActivity extends BaseActivity implements View.OnClickListener {

    private AutoCompleteTextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pwd);

        Button btnFind = (Button) findViewById(R.id.findpwd_button);
        btnFind.setOnClickListener(this);

        Button back = (Button) findViewById(R.id.head_back);
        back.setOnClickListener(this);

        userName = (AutoCompleteTextView) findViewById(R.id.findpwd_username);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_1line);
        ArrayList<UserModel> alluser = UserModel.getAll(this);
        for (UserModel model : alluser) {
            adapter.add(model.getUsername());
        }
        userName.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        ActivityMgr.getInstance().navigateToSeastarLoginAndFinish(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.findpwd_button) {
            String name = userName.getText().toString();

            // 检查输入是否符合要求
            if (name.isEmpty() || !name.matches("[a-zA-Z][a-zA-Z0-9]{5,60}")) {
                Toast.makeText(this, R.string.tip_username_err, Toast.LENGTH_SHORT).show();
                return;
            }

            AuthHelper.getInstance().doFindPwd(name);
            Toast.makeText(this, R.string.tip_findpwd_success, Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.head_back) {
            ActivityMgr.getInstance().navigateToSeastarLoginAndFinish(this);
        }
    }
}
