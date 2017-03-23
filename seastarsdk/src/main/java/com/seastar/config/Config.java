package com.seastar.config;

import java.net.PortUnreachableException;

/**
 * Created by osx on 16/11/7.
 */

public class Config {
    public static String TAG = "Seastar";

    public static int TYPE_ACCOUNT = 0;
    public static int TYPE_GUEST = 1;
    public static int TYPE_GOOGLE = 2;
    public static int TYPE_GAMECENTER = 3;
    public static int TYPE_FACEBOOK = 4;

    public static int OP_LOGIN = 0;
    public static int OP_REGIST = 1;

    public static int REQUEST_CODE_MAINLOGIN = 1;
    public static int REQUEST_CODE_RELOGIN = 2;
    public static int REQUEST_CODE_LOGIN = 3;
    public static int REQUEST_CODE_REGISTER = 4;
    public static int REQUEST_CODE_FIND = 5;
    public static int REQUEST_CODE_WEBVIEW = 6;

    public static int RESULT_CODE_SUCCESS = 1;
    public static int RESULT_CODE_FAIL = 2;
    public static int RESULT_CODE_BACK = 3;
    public static int RESULT_CODE_GOOGLE = 4;
    public static int RESULT_CODE_MYCARD = 5;
    public static int RESULT_CODE_PAYPAL = 6;

}
