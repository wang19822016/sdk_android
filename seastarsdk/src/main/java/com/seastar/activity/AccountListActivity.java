package com.seastar.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.seastar.R;
import com.seastar.config.Config;
import com.seastar.helper.BossHelper;
import com.seastar.listener.ListenerMgr;
import com.seastar.model.UserModel;
import com.seastar.utils.ActivityMgr;

import java.util.ArrayList;
import java.util.List;

public class AccountListActivity extends BaseActivity implements View.OnClickListener, Spinner.OnItemSelectedListener {
    private AccountListAdapter adapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_list);

        spinner = (Spinner) findViewById(com.seastar.R.id.account_spinner);
        spinner.setOnItemSelectedListener(this);

        adapter = new AccountListAdapter(this, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        Button back = (Button) findViewById(R.id.head_back);
        back.setVisibility(View.INVISIBLE);

        Button login = (Button) findViewById(R.id.account_list_btn_login);
        login.setOnClickListener(this);

        TextView otherLogin = (TextView) findViewById(R.id.account_list_text);
        otherLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        otherLogin.setOnClickListener(this);

        // 填充数据
        ArrayList<UserModel> userModels = UserModel.getAll(this);

        UserModel model = UserModel.getCurrentUser(this);
        if (model != null) {
            if (model.getLoginType() == Config.TYPE_GUEST) {
                adapter.add(new AccountListItem(R.drawable.guest, model.getUsername(), model.getUserId(), model.getToken()));
            } else if (model.getLoginType() == Config.TYPE_ACCOUNT) {
                adapter.add(new AccountListItem(R.drawable.seastar, model.getUsername(), model.getUserId(), model.getToken()));
            } else if (model.getLoginType() == Config.TYPE_FACEBOOK) {
                adapter.add(new AccountListItem(R.drawable.facebook, model.getUsername(), model.getUserId(), model.getToken()));
            } else {
                adapter.add(new AccountListItem(R.drawable.google, model.getUsername(), model.getUserId(), model.getToken()));
            }
        }

        for (UserModel tmpModel : userModels) {
            if (model != null && tmpModel.getUserId() == model.getUserId())
                continue;
            if (tmpModel.getLoginType() == Config.TYPE_GUEST) {
                adapter.add(new AccountListItem(R.drawable.guest, tmpModel.getUsername(), tmpModel.getUserId(), tmpModel.getToken()));
            } else if (tmpModel.getLoginType() == Config.TYPE_ACCOUNT) {
                adapter.add(new AccountListItem(R.drawable.seastar, tmpModel.getUsername(), tmpModel.getUserId(), tmpModel.getToken()));
            } else if (tmpModel.getLoginType() == Config.TYPE_FACEBOOK) {
                adapter.add(new AccountListItem(R.drawable.facebook, tmpModel.getUsername(), tmpModel.getUserId(), tmpModel.getToken()));
            } else {
                adapter.add(new AccountListItem(R.drawable.google, tmpModel.getUsername(), tmpModel.getUserId(), tmpModel.getToken()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        ListenerMgr.getInstance().getLoginFinishListener().onFinished(false, "", "");
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onClick(View v) {
        if (v.getId() == R.id.account_list_text) {
            ActivityMgr.getInstance().navigateToLoginChannelAndFinish(this);
        } else if (v.getId() == R.id.account_list_btn_login) {
            if (spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
                UserModel userModel = UserModel.get(this, adapter.getItem(spinner.getSelectedItemPosition()).getUserId());
                userModel.save(this, true);

                finish();

                // 登录成功
                ListenerMgr.getInstance().getLoginFinishListener().onFinished(true,
                        userModel.getUserId() + "",
                        userModel.getToken());

                BossHelper.getInstance().postLogin(userModel.getUserId());
            }
        }
    }

    public class AccountListItem {
        private int headImageId;
        private String username;
        private long userId;
        private String token;

        public AccountListItem(int headImageId, String username, long userId, String token) {
            this.headImageId = headImageId;
            this.username = username;
            this.userId = userId;
            this.token = token;
        }

        public int getHeadImageId() {
            return headImageId;
        }

        public String getUsername() {
            return username;
        }

        public long getUserId() { return userId; }

        public String getToken() { return token; }
    }

    public class AccountListAdapter extends ArrayAdapter<AccountListItem> {
        private List<AccountListItem> list = new ArrayList<>();

        public AccountListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public View getCustomView(final int position, View convertView, ViewGroup parent, boolean hide) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.spinner_item, parent, false);
            }

            ImageView headImage = (ImageView)convertView.findViewById(R.id.spinner_head_image);
            headImage.setImageResource(getItem(position).getHeadImageId());

            TextView text = (TextView)convertView.findViewById(R.id.spinner_text);
            text.setText(getItem(position).getUsername());

            ImageView deleteImage = (ImageView)convertView.findViewById(R.id.spinner_delete_image);
            deleteImage.setImageResource(R.drawable.cancel);
            deleteImage.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除帐号
                    UserModel userModel = UserModel.getCurrentUser(AccountListActivity.this);
                    if (userModel != null && getItem(position).getUserId() == userModel.getUserId()) {
                        UserModel.removeCurrentUser(AccountListActivity.this);
                    }
                    UserModel.remove(AccountListActivity.this, getItem(position).getUserId());
                    adapter.remove(getItem(position));
                    if (adapter.isEmpty()) {
                        ActivityMgr.getInstance().showLoginChannelBack(false);
                        ActivityMgr.getInstance().navigateToLoginChannelAndFinish(AccountListActivity.this);
                    }
                }
            });
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, true);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent, false);
        }
    }
}
