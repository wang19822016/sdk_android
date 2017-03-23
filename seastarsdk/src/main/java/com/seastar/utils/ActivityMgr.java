package com.seastar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.seastar.activity.AccountListActivity;
import com.seastar.activity.BindEmailActivity;
import com.seastar.activity.FindPwdActivity;
import com.seastar.activity.LoginChannelActivity;
import com.seastar.activity.SeastarLoginActivity;
import com.seastar.activity.SeastarRegistActivity;

import java.util.ArrayList;

/**
 * Created by osx on 17/3/17.
 */

public class ActivityMgr {
    private static ActivityMgr instance = new ActivityMgr();
    private boolean showBack = true;

    private ArrayList<Activity> activities = new ArrayList<>();

    public static ActivityMgr getInstance() {
        return instance;
    }

    public boolean showLoginChannelBack() {
        return showBack;
    }

    public void showLoginChannelBack(boolean showBack) {
        this.showBack = showBack;
    }

    public void add(Activity activity) {
        activities.add(activity);
    }

    public void remove(Activity activity) {
        activities.remove(activity);
    }

    public void finish(Activity activity) {
        activities.remove(activity);
        activity.finish();
    }

    public void finishAll() {
        for (Activity activity : activities) {
            activity.finish();
        }

        activities.clear();
    }

    public void navigateToLoginChannel(Context context) {
        Intent intent = new Intent(context, LoginChannelActivity.class);
        context.startActivity(intent);
    }

    public void navigateToAccountList(Context context) {
        Intent intent = new Intent(context, AccountListActivity.class);
        context.startActivity(intent);
    }

    public void navigateToLoginChannelAndFinish(Activity activity) {
        Intent intent = new Intent(activity, LoginChannelActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void navigateToAccountListAndFinish(Activity activity) {
        Intent intent = new Intent(activity, AccountListActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void navigateToSeastarLoginAndFinish(Activity activity) {
        Intent intent = new Intent(activity, SeastarLoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void navigateToFindPwdAndFinish(Activity activity) {
        Intent intent = new Intent(activity, FindPwdActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void navigateToSeastarRegistAndFinish(Activity activity) {
        Intent intent = new Intent(activity, SeastarRegistActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void navigateToBindEmail(Activity activity) {
        Intent intent = new Intent(activity, BindEmailActivity.class);
        activity.startActivity(intent);
    }
}
