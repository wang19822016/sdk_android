package com.seastar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seastar.R;
import com.seastar.helper.AuthHelper;

/**
 * Created by osx on 17/3/22.
 */

public class BindEmailActivity extends BaseActivity implements View.OnClickListener {
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_email);

        Button bind = (Button) findViewById(R.id.bind_email_button);
        bind.setOnClickListener(this);

        Button cancel = (Button) findViewById(R.id.bind_email_cancel_button);
        cancel.setOnClickListener(this);

        editText = (EditText) findViewById(R.id.bind_email_text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bind_email_button) {
            String email = editText.getText().toString();

            if (email.isEmpty() || !email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
                Toast.makeText(this, R.string.tip_email_err, Toast.LENGTH_SHORT).show();
                return;
            }

            AuthHelper.getInstance().updateEmail(email);
            finish();
        } else {
            finish();
        }
    }
}
