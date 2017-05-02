package com.seastar.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by osx on 16/11/7.
 */

public class AppModel {
    private int appId;
    private String appKey;
    private String serverUrl;
    private String appsflyerGcmProjectId;
    private String appsflyerKey;
    private String goCpaAppId;
    private int goCpaAdvertiserId;
    private boolean goCpaReferral;

    public AppModel(Context context) {

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            appId = appInfo.metaData.getInt("st_app_id", 0);
            appKey = appInfo.metaData.getString("st_app_key", "");
            serverUrl = appInfo.metaData.getString("st_server_url", "");
            appsflyerGcmProjectId = appInfo.metaData.getString("appsflyer_gcm_project_id", "");
            appsflyerKey = appInfo.metaData.getString("appsflyer_key", "");
            goCpaAppId = appInfo.metaData.getString("gocpa_app_id", "");
            goCpaAdvertiserId = appInfo.metaData.getInt("gocpa_advertiser_id", 0);
            goCpaReferral = appInfo.metaData.getBoolean("gocpa_referral", false);
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public int getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getAppsflyerGcmProjectId() {
        return appsflyerGcmProjectId;
    }

    public String getAppsflyerKey() {
        return appsflyerKey;
    }

    public String getGoCpaAppId() {
        return goCpaAppId;
    }

    public int getGoCpaAdvertiserId() {
        return goCpaAdvertiserId;
    }

    public Boolean getGoCpaReferral() {
        return goCpaReferral;
    }
}
