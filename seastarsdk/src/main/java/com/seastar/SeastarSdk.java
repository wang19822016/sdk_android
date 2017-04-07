package com.seastar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.anjlab.android.iab.v3.SkuDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.gocpa.android.sdk.GocpaTracker;
import com.gocpa.android.sdk.GocpaUtil;
import com.seastar.activity.WebViewActivity;
import com.seastar.config.Config;
import com.seastar.helper.AuthHelper;
import com.seastar.helper.BossHelper;
import com.seastar.helper.FacebookHelper;
import com.seastar.helper.GoogleApiClientHelper;
import com.seastar.helper.GooglePayHelper;
import com.seastar.helper.MyCardPayHelper;
import com.seastar.listener.ListenerMgr;
import com.seastar.model.AppModel;
import com.seastar.model.UserModel;
import com.seastar.listener.OnActionFinishListener;
import com.seastar.listener.OnActionTwoFinishListener;
import com.seastar.listener.OnLoginFinishListener;
import com.seastar.listener.OnPurchaseFinishListener;
import com.seastar.utils.ActivityMgr;
import com.seastar.utils.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by osx on 16/11/7.
 */

public class SeastarSdk {
    public static SeastarSdk current = new SeastarSdk();
    private Activity activity;

    private boolean initedAppsflyer = false;
    private boolean initedGocpa = false;


    private OnPurchaseFinishListener middlePurchaseFinishListener = new OnPurchaseFinishListener() {
        @Override
        public void onFinished(boolean bool, String order) {
            ListenerMgr.getInstance().getPurchaseFinishListener().onFinished(bool, order);
            if (bool) {
                Utility.showDialog(activity, activity.getString(R.string.tip_pay_success));
            }
        }
    };

    public void onCreate(final Activity activity, Bundle savedInstanceState) {
        this.activity = activity;

        GoogleApiClientHelper.getInstance().init(activity.getApplicationContext());
        GooglePayHelper.getInstance().init(activity.getApplicationContext());
        FacebookHelper.getInstance().init(activity.getApplicationContext());
        MyCardPayHelper.getInstance().init(activity.getApplicationContext());
        AuthHelper.getInstance().init(activity.getApplicationContext());
        BossHelper.getInstance().init(activity);

        GoogleApiClientHelper.getInstance().connect();

        initAppsFlyer();
        initGoCpa();

        UserModel.clearExpire(activity);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FacebookHelper.getInstance().onActivityResult(requestCode, resultCode, data);
        GooglePayHelper.getInstance().onActivityResult(requestCode, resultCode, data);
        MyCardPayHelper.getInstance().onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.REQUEST_CODE_WEBVIEW) {
            if (resultCode == Config.RESULT_CODE_GOOGLE) {
                String productId = data.getStringExtra("productId");
                String roleId = data.getStringExtra("roleId");
                String serverId = data.getStringExtra("serverId");
                String extra = data.getStringExtra("extra");
                GooglePayHelper.getInstance().doPurchase(activity, productId, roleId, serverId, extra, middlePurchaseFinishListener);
            } else if (resultCode == Config.RESULT_CODE_MYCARD) {
                String productId = data.getStringExtra("productId");
                String roleId = data.getStringExtra("roleId");
                String serverId = data.getStringExtra("serverId");
                String extra = data.getStringExtra("extra");
                MyCardPayHelper.getInstance().doPurchase(activity, productId, roleId, serverId, extra, middlePurchaseFinishListener);
            } else if (resultCode == Config.RESULT_CODE_PAYPAL) {
                String productId = data.getStringExtra("productId");
                String roleId = data.getStringExtra("roleId");
                String serverId = data.getStringExtra("serverId");
                String extra = data.getStringExtra("extra");
                String order = data.getStringExtra("order");
                boolean success = data.getBooleanExtra("success", false);
                ListenerMgr.getInstance().getPurchaseFinishListener().onFinished(success, order);
            } else {
                ListenerMgr.getInstance().getPurchaseFinishListener().onFinished(false, "");
            }
        }
    }

    public void onResume() {
        FacebookHelper.getInstance().onResume(activity);
    }

    public void onPause() {

    }

    public void onDestory() {
    }

    public void checkLeakPurchase() {
        MyCardPayHelper.getInstance().checkLeakPurchase(activity);
    }

    public void login(final OnLoginFinishListener linstener) {
        UserModel model = UserModel.getCurrentUser(activity);
        if (model != null) {
            linstener.onFinished(true, model.getUserId() + "", model.getToken());
            showLoginToast(model.getLoginType(), model.getUsername());

            showBindEmail(model.getLoginType());

            BossHelper.getInstance().postLogin(model.getUserId());
        } else {
            ListenerMgr.getInstance().setLoginFinishListener(new OnLoginFinishListener() {
                @Override
                public void onFinished(boolean bool, String userId, String session) {
                    linstener.onFinished(bool, userId, session);
                    if (bool) {
                        UserModel model = UserModel.getCurrentUser(activity);
                        showLoginToast(model.getLoginType(), model.getUsername());

                        showBindEmail(model.getLoginType());
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, R.string.tip_login_err, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            if (UserModel.getAll(activity).isEmpty()) {
                // 启动登录渠道界面
                ActivityMgr.getInstance().showLoginChannelBack(false);
                ActivityMgr.getInstance().navigateToLoginChannel(activity);
            } else {
                // 启动帐号选择界面
                ActivityMgr.getInstance().showLoginChannelBack(true);
                ActivityMgr.getInstance().navigateToAccountList(activity);
            }
        }
    }

    public void purchase(String productId, String roleId, String serverId, String extra, final OnPurchaseFinishListener linstener) {
        UserModel userModel = UserModel.getCurrentUser(activity);
        if (userModel != null) {
            ListenerMgr.getInstance().setPurchaseFinishListener(linstener);

            if (userModel.getPayType() == 0x01) {
                GooglePayHelper.getInstance().doPurchase(activity, productId, roleId, serverId, extra, middlePurchaseFinishListener);
            } else if (userModel.getPayType() == 0x02) {
                MyCardPayHelper.getInstance().doPurchase(activity, productId, roleId, serverId, extra, middlePurchaseFinishListener);
            } else {
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("roleId", roleId);
                intent.putExtra("serverId", serverId);
                intent.putExtra("extra", extra);
                activity.startActivityForResult(intent, Config.REQUEST_CODE_WEBVIEW);
            }
        }
    }

    public void switchAccount(final OnLoginFinishListener linstener) {
        ListenerMgr.getInstance().setLoginFinishListener(new OnLoginFinishListener() {
            @Override
            public void onFinished(boolean bool, String userId, String session) {
                linstener.onFinished(bool, userId, session);
                if (bool) {
                    UserModel model = UserModel.getCurrentUser(activity);
                    showLoginToast(model.getLoginType(), model.getUsername());
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, R.string.tip_login_err, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        if (UserModel.getAll(activity).isEmpty()) {
            // 启动登录渠道界面
            ActivityMgr.getInstance().showLoginChannelBack(false);
            ActivityMgr.getInstance().navigateToLoginChannel(activity);
        } else {
            // 启动帐号选择界面
            ActivityMgr.getInstance().showLoginChannelBack(true);
            ActivityMgr.getInstance().navigateToAccountList(activity);
        }
    }

    public void showFacebookSocialDialog(String bindUrl, String mainPageUrl, String shareUrl, String shareImageUrl, String shareTitle, String shareDescription) {
        ActivityMgr.getInstance().navigateToFacebookSocialActivity(activity, bindUrl, mainPageUrl, shareUrl, shareImageUrl, shareTitle, shareDescription);
    }

    public void shareFb(String imageUri, String caption, final OnActionFinishListener listener) {
        FacebookHelper.getInstance().share(activity, imageUri, caption, listener);
    }

    public void shareFb(Bitmap bitmap, String caption, final OnActionFinishListener listener) {
        FacebookHelper.getInstance().share(activity, bitmap, caption, listener);
    }

    public void shareFb(String contentURL, String contentTitle, String imageURL, String contentDescription, final OnActionFinishListener listener) {
        FacebookHelper.getInstance().share(activity, contentURL, contentTitle, imageURL, contentDescription, listener);
    }

    public void doFbGameRequest(String title, String message, final OnActionFinishListener listener) {
        FacebookHelper.getInstance().doGameRequest(activity, title, message, listener);
    }

    public void deleteFbGameRequest() {
        FacebookHelper.getInstance().deleteGameRequest();
    }

    public void doFbAppInvite(String applinkUrl, String previewImageUrl, final OnActionFinishListener listener) {
        FacebookHelper.getInstance().doAppInvite(activity, applinkUrl, previewImageUrl, listener);
    }

    public void getFbFriendInfo(String height, String width, final String limit, final OnActionTwoFinishListener listener) {
        FacebookHelper.getInstance().getFriendInfo(height, width, limit, listener);
    }

    public void getNextFbFriendInfo(final OnActionTwoFinishListener listener) {
        FacebookHelper.getInstance().getNextFriendInfo(listener);
    }

    public void getPrevFbFriendInfo(final OnActionTwoFinishListener listener) {
        FacebookHelper.getInstance().getPrevFriendInfo(listener);
    }

    public void getFbMeInfo(final OnActionTwoFinishListener listener) {
        FacebookHelper.getInstance().getMeInfo(listener);
    }

    public void unlockGoogleAchievement(String achievementId) {
        GoogleApiClientHelper.getInstance().unlockAchievement(achievementId);
    }

    public void incrementGoogleAchievement(String achievementId, int score) {
        GoogleApiClientHelper.getInstance().incrementAchievement(achievementId, score);
    }

    public void showGoogleAchievement() {
        GoogleApiClientHelper.getInstance().showAchievement(activity);
    }

    public void updateGoogleScoreOnLeaderboard(String leaderboardId, int score) {
        GoogleApiClientHelper.getInstance().updateScoreOnLeaderboard(leaderboardId, score);
    }

    public void showGoogleLeaderboard() {
        GoogleApiClientHelper.getInstance().showLeaderboard(activity);
    }

    public void logout() {
        FacebookHelper.getInstance().logout();
        // 删除当前的user
        UserModel.removeCurrentUser(activity);
    }

    public void trackLevelAchieved(int level, int score) {
        if (initedAppsflyer) {
            Map<String, Object> eventValue = new HashMap<>();
            eventValue.put(AFInAppEventParameterName.LEVEL, level);
            eventValue.put(AFInAppEventParameterName.SCORE, score);
            AppsFlyerLib.getInstance().trackEvent(activity, AFInAppEventType.LEVEL_ACHIEVED, eventValue);
        }

        if (initedGocpa) {
            GocpaTracker.getInstance(activity).reportEvent("Levelup");
        }
    }

    public void trackPurchase(float price, String skuType, String sku, String currency) {
        if (initedAppsflyer) {
            Map<String, Object> eventValue = new HashMap<String, Object>();
            eventValue.put(AFInAppEventParameterName.REVENUE, price);
            eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, skuType);
            eventValue.put(AFInAppEventParameterName.CONTENT_ID, sku);
            eventValue.put(AFInAppEventParameterName.CURRENCY, currency);
            AppsFlyerLib.getInstance().trackEvent(activity, AFInAppEventType.PURCHASE, eventValue);
        }

        if (initedGocpa) {
            GocpaTracker.getInstance(activity).reportEvent("Purchase", price, currency);
        }
    }

    public void trackRegistration() {
        if (initedAppsflyer) {
            Map<String, Object> eventValue = new HashMap<String, Object>();
            AppsFlyerLib.getInstance().trackEvent(activity, AFInAppEventType.COMPLETE_REGISTRATION, eventValue);
        }

        if (initedGocpa) {
            GocpaTracker.getInstance(activity).reportEvent("Register");
        }
    }

    public void trackLogin() {
        if (initedAppsflyer) {
            Map<String, Object> eventValue = new HashMap<String, Object>();
            AppsFlyerLib.getInstance().trackEvent(activity, AFInAppEventType.LOGIN, eventValue);
        }

        if (initedGocpa) {
            GocpaTracker.getInstance(activity).reportEvent("Login");
        }
    }

    public void trackCustom(String eventName, Map<String, Object> eventValue) {
        if (initedAppsflyer) {
            AppsFlyerLib.getInstance().trackEvent(activity, eventName, eventValue);
        }

        if (initedGocpa) {
            GocpaTracker.getInstance(activity).reportEvent(eventName);
        }
    }

    public SkuDetails getPurchaseSkuDetails(String sku) {
        return GooglePayHelper.getInstance().getPurchaseSkuDetails(sku);
    }

    public SkuDetails getSubscriptionSkuDetails(String sku) {
        return GooglePayHelper.getInstance().getSubscriptionSkuDetails(sku);
    }

    private void initAppsFlyer() {
        try {
            AppModel appModel = new AppModel(activity);

            if (!appModel.getAppsflyerKey().isEmpty()) {
                initedAppsflyer = true;
                AppsFlyerLib.getInstance().setCustomerUserId(appModel.getAppId() + "");
                AppsFlyerLib.getInstance().startTracking(activity.getApplication(), appModel.getAppsflyerKey());
                BossHelper.getInstance().appsFlyerUID = AppsFlyerLib.getInstance().getAppsFlyerUID(activity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initGoCpa() {
        try {
            AppModel appModel = new AppModel(activity);
            if (!appModel.getGoCpaAppId().isEmpty()) {
                initedGocpa = true;
                GocpaUtil.setAppId(appModel.getGoCpaAppId());
                GocpaUtil.setAdvertiserId(appModel.getGoCpaAdvertiserId() + "");
                GocpaUtil.setReferral(appModel.getGoCpaReferral());

                GocpaTracker.getInstance(activity).reportDevice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginToast(int type, String username) {
        int resId = R.drawable.seastar;
        if (type == Config.TYPE_GUEST)
            resId = R.drawable.guest;
        else if (type == Config.TYPE_FACEBOOK)
            resId = R.drawable.facebook;
        else if (type == Config.TYPE_GOOGLE)
            resId = R.drawable.google;
        Utility.showToast(activity, resId, username + activity.getString(R.string.tip_welcome), 5);
    }

    private void showBindEmail(int type) {
        if (type != Config.TYPE_GUEST && type != Config.TYPE_ACCOUNT) return;

        int n = new Random().nextInt(100);
        if (n < 60) return;

        AuthHelper.getInstance().hasEmail(new OnActionFinishListener() {
            @Override
            public void onFinished(boolean success) {
                if (!success) {
                    ActivityMgr.getInstance().navigateToBindEmail(activity);
                }
            }
        });
    }
}
