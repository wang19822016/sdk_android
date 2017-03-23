# 1. 添加权限:
```java
<uses-permission android:name="com.android.vending.BILLING" />

<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<!-- 基本权限 开始 -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="com.android.vending.BILLING" />

<uses-permission android:name="android.permission.READ_PHONE_STATE" />

<!-- MYCARD -->
<uses-permission android:name="android.permission.CAMERA" />

<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />

<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FLASHLIGHT" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<uses-permission android:name="android.permission.READ_LOGS" />
<!-- MYCARD -->

<uses-permission android:name="android.permission.WAKE_LOCK" />

<!-- 注意：：：将此处的<YOUR-PACKAGE-NAME>换成你的包名 -->
<permission android:name="<YOUR-PACKAGE-NAME>.permission.C2D_MESSAGE" android:protectionLevel="signature" />
<uses-permission android:name="<YOUR-PACKAGE-NAME>.permission.C2D_MESSAGE" />
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<!-- 基本权限 结束 -->

```

# 2. 在application节点添加android:name属性:
```java

android:name="com.seastar.SeastarApplication"

```

# 3. 添加Activity:
```java

<!-- 基础Activity, screenOrientation可以根据需要进行调整, 支持横屏和竖屏 -->
<activity
    android:name="com.seastar.activity.LoginChannelActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:launchMode="standard"
    android:screenOrientation="portrait"
    android:theme="@style/CustomDialog" />
<activity
    android:name="com.seastar.activity.SeastarLoginActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:launchMode="standard"
    android:screenOrientation="portrait"
    android:theme="@style/CustomDialog" />
<activity
    android:name="com.seastar.activity.SeastarRegistActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:launchMode="standard"
    android:screenOrientation="portrait"
    android:theme="@style/CustomDialog" />
<activity
    android:name="com.seastar.activity.FindPwdActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:launchMode="standard"
    android:screenOrientation="portrait"
    android:theme="@style/CustomDialog" />
<activity
    android:name="com.seastar.activity.AccountListActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:launchMode="standard"
    android:screenOrientation="portrait"
    android:theme="@style/CustomDialog" />
<activity
    android:name="com.seastar.activity.WebViewActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:launchMode="standard"
    android:screenOrientation="portrait" />


<!-- facebook activity -->
<activity
    android:name="com.facebook.FacebookActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:screenOrientation="portrait"
    android:label="@string/app_name" />

<!-- MYCARD -->
<activity
    android:name="soft_world.mycard.paymentapp.ui.SplashActivity"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="soft_world.mycard.paymentapp.ui.MainActivity"
    android:screenOrientation="portrait"
    android:windowSoftInputMode="adjustPan" >
</activity>
<activity
    android:name="soft_world.mycard.paymentapp.ui.TrainActivity"
    android:screenOrientation="portrait" >
</activity>
<!-- zxing -->
<activity
    android:name="com.google.zxing.CaptureActivity"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="tw.com.mycard.paymentsdk.PSDKActivity"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="soft_world.mycard.paymentapp.ui.billing.BillingWebViewActivity"
    android:configChanges="orientation"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Dialog" >
</activity>
<!-- 異康 -->
<activity
    android:name="soft_world.mycard.paymentapp.Ecom.ATMMenuActivity"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="com.xmobilepay.xpaymentlibs.XCardTypeForm"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="com.xmobilepay.xpaymentlibs.XPayCardPassWordForm"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="com.xmobilepay.xpaymentlibs.XSmallPayCardPassWordForm"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="com.xmobilepay.xpaymentlibs.PaymentResultForm"
    android:screenOrientation="portrait" >
</activity>
<activity
    android:name="com.xmobilepay.xpaymentlibs.PaymentErrResultForm"
    android:screenOrientation="portrait" >
</activity>
<!-- 遠傳電信 -->
<activity
    android:name="com.fet.iap.activity.FetLoginActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:windowSoftInputMode="adjustPan" >
</activity>

<!-- 中華電信 -->
<activity android:name="com.cht.iap.api.ChtRegMainActivity" />
<activity android:name="com.cht.iap.api.ChtPhoneNumPayConfirmActivity" />
<activity android:name="com.cht.iap.api.ChtRegEInvoiceInfo" />
<activity android:name="com.cht.iap.api.ChtRegVerifyOTP" />
<activity android:name="com.cht.iap.api.ChtRegHNDataTabActivity" />
<activity android:name="com.cht.iap.api.ChtRegHNAccountActivity" />
<activity android:name="com.cht.iap.api.ChtRegMobileAuth" />
<activity android:name="com.cht.iap.api.ChtRegMobileHNData" />
<activity android:name="com.cht.iap.api.ChtTransactionAuth" />
<activity android:name="com.cht.iap.api.ChtRegVerifyMessage" />
<!-- 中國信託 -->
<activity
    android:name="com.softmobile.ui.PayPageActivity"
    android:configChanges="orientation"
    android:screenOrientation="portrait" />
<!-- 首信易 -->
<activity
    android:name="com.payeasenet.token.lib.ui.TokenPayTypeCheckUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.CardTypeCheckUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.TokenCreateUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" >
</activity>
<activity
    android:name="com.payeasenet.token.lib.ui.TokenCreateResultUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.TokenPayUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEPayRelUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.TokenIntroductionUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" >
</activity>
<activity
    android:name="com.payeasenet.token.lib.ui.TokenUnBindedUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" >
</activity>
<activity
    android:name="com.payeasenet.token.lib.ui.MoreAboutUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEUpopInfoUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEUpopPayUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEIvrPayUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEQuickPayUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEUpmpPayUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEVisaPayUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEVisaInfoUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEVisaBillInfoUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEDebitBillInfoUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEQuickInfoUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<activity
    android:name="com.payeasenet.token.lib.ui.PEUpmpInfoUI"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Light.NoTitleBar" />
<!-- MYCARD -->

<!-- appsflyer接收器 -->
<receiver android:name="com.appsflyer.MultipleInstallBroadcastReceiver" android:exported="true">
    <intent-filter>
        <action android:name="com.android.vending.INSTALL_REFERRER" />
    </intent-filter>
</receiver>
<receiver android:name="com.google.android.gms.gcm.GcmReceiver" android:exported="true">
      <intent-filter>
            <action android:name="com.google.android.c2dm.intent.RECEIVE" />
      </intent-filter>
</receiver>
<service android:name="com.appsflyer.InstanceIDListener" android:exported="false">
      <intent-filter>
            <action android:name="com.google.android.gms.iid.InstanceID"/>
      </intent-filter>
</service>

<!-- gocpa receiver -->
<receiver android:name="com.gocpa.android.sdk.GocpaPlayMarketTracker" android:exported="true">
    <intent-filter>
        <action android:name="com.android.vending.INSTALL_REFERRER" />
    </intent-filter>
</receiver>
```

# 4. 添加配置
```java

<!-- 配置google game id，需要在strings.xml内部添加google_game_id项，设置value为谷歌后台提供的game id -->
<meta-data
    android:name="com.google.android.gms.games.APP_ID"
    android:value="@string/google_game_id" />

<!-- 配置facebook app id，需要在strings.xml内部添加facebook_app_id项，设置value为fb后台提供的应用id -->
<meta-data
    android:name="com.facebook.sdk.ApplicationId"
    android:value="@string/facebook_app_id" />

<!-- 配置谷歌services版本，不要改动 -->
<meta-data
    android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />

<!-- 配置分配的应用id -->
<meta-data
    android:name="st_app_id"
    android:value="1" />
<!-- 配置分配的客户端密钥 -->
<meta-data
    android:name="st_app_key"
    android:value="0e2d30ab2ec5a7b518bfce9680ecb1fe" />
<!-- 配置服务器api地址，不要改动 -->
<meta-data
    android:name="st_server_url"
    android:value="https://52.77.192.179" />
<!-- 配置appsflyer的key -->
<meta-data
    android:name="appsflyer_key"
    android:value="L4ff7yygMdy6XL88YXjrF" />
<!-- 配置appsflyer的gcm project id -->
<meta-data
    android:name="appsflyer_gcm_project_id"
    android:value="xxxxxx" />

<!-- 配置gocpa的appid -->
<meta-data
    android:name="gocpa_app_id"
    android:value="xxxxxx" />
<!-- 配置gocpa的advertiser_id -->
<meta-data
    android:name="gocpa_advertiser_id"
    android:value="xxxxxx" />
<!-- 配置gocpa的referral -->
<meta-data
    android:name="gocpa_referral"
    android:value="false" />
```

# 5. androidstudio引用库:
* 将seastarsdk.aar放入libs.
* build.gradle内dependencies添加:
```java
compile(name: 'facebook-android-sdk-4.17.0', ext: 'aar')
compile 'com.squareup.okhttp3:okhttp:3.4.1'
compile 'com.google.android.gms:play-services-base:9.6.0'
compile 'com.google.android.gms:play-services-basement:9.6.0'
compile 'com.google.android.gms:play-services-games:9.6.0'
compile 'com.appsflyer:af-android-sdk:4+@aar'
```
* build.gradle内添加插件:
```java

apply plugin: 'com.google.gms.google-services'

```

# 6. 添加运行时方法:
```java

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 创建
    SeastarSdk.current.onCreate(this, savedInstanceState);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    SeastarSdk.current.onActivityResult(requestCode, resultCode, data);
}

@Override
protected void onDestroy() {
    super.onDestroy();

    SeastarSdk.current.onDestory();
}

```

＃ 7. 登录
```java

SeastarSdk.current.login(new OnLoginFinishListener() {
    @Override
    public void onFinished(final boolean bool, final String userId, String session) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 登录成功可以获得帐号ID, ID为数字
            }
        });
    }
});

```

# 8. 登出
```java

SeastarSdk.current.logout();

```

# 9. 支付
```java

SeastarSdk.current.purchase("商品id", "游戏内角色id", "充值服务器id", "附加数据", new OnPurchaseFinishListener() {
    @Override
    public void onFinished(final boolean bool, final String order) {
        // 支付成功可以获得本次支付的流水号
    }
});

```

# 10. 切换帐号
```java

SeastarSdk.current.switchAccount(new OnLoginFinishListener() {
    @Override
    public void onFinished(final boolean bool, final String userId, String session) {
        // 成功可以获得帐号ID, ID为数字
    }
});

```

# 11. 统计接口
```java

// 升级接口
// level: 当前等级
// score: 当前经验，可以默认为0
void trackLevelAchieved(int level, int score);

// 支付接口
// price： 商品价格
// skuType: 商品类型，如月卡、普通商品等，可以自定义
// sku: 商品ID
// currency: 充值货币，可以默认为USD，price按照USD算
void trackPurchase(float price, String skuType, String sku, String currency);

// 角色注册接口
void trackRegistration();

// 角色登录接口
void trackLogin();

// 自定义事件
void trackCustom(String eventName, Map<String, Object> eventValue);

```

# 12. facebook 分享接口
```java

// 分享图片
// imageUri: 图片URI，需要在网站上生成一个url，分享时uri就是这个url
// caption 分享标题
void shareFb(String imageUri, String caption, final OnActionFinishListener listener);

// 分享图片
// bitmap: 本地图片对象
// caption: 分享标题
void shareFb(Bitmap bitmap, String caption, final OnActionFinishListener listener);

// 分享链接
// contentURL: 分享的链接，如果是商店内app应用地址，用户不能添加分享文字，分享内容中生成一个商店截图,
//      如果是自己制作的网页，需要按照fb要求添加fb的tag，此时用户可以添加自己的分享文字。
// contentTitle: 分享标题
// imageURL: 分享内容中显示的图片的地址，可以不添加，在contentURL为商店应用页面链接时不起作用
// contentDescription: 默认的分享文字
void shareFb(String contentURL, String contentTitle, String imageURL, String contentDescription, final OnActionFinishListener listener);

// 游戏邀请
// title: 邀请标题
// message: 邀请信息内容
// 会弹出窗口，选中邀请的好友
void doFbGameRequest(String title, String message, final OnActionFinishListener listener);

// 删除邀请信息
// 每次启动时调用，可以删除邀请信息
void deleteFbGameRequest();

```

# 13. facebook信息接口
```java

// 以下接口返回一个json串，从其中可以解析出数据

// 获取好友信息
// height: 头像高度
// width: 头像宽度
// limit: 每次获得的好友条数
void getFbFriendInfo(String height, String width, final String limit, final OnActionTwoFinishListener listener);

// 获取下一批好友信息
void getNextFbFriendInfo(final OnActionTwoFinishListener listener);

// 获取上一批好友信息
void getPrevFbFriendInfo(final OnActionTwoFinishListener listener)

// 获得个人信息
void getFbMeInfo(final OnActionTwoFinishListener listener)

```

# 14. google成就和积分墙接口
```java

// 解锁成就
// achievementId: 成就id
void unlockGoogleAchievement(String achievementId);

// 显示成就列表
void showGoogleAchievement();

// 上传积分
// leaderboardId: 积分墙id
// score: 本次获得积分
void updateGoogleScoreOnLeaderboard(String leaderboardId, int score);

// 显示积分墙
void showGoogleLeaderboard();


```
