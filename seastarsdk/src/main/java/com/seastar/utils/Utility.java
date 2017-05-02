package com.seastar.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seastar.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by osx on 16/11/7.
 */

public class Utility {
    public static String b64encode(String s) {
        s = s.trim();
        if (s.isEmpty())
            return s;
        try {
            return new String(Base64.encode(s.getBytes("UTF-8"), Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return s;
    }

    public static String b64decode(String s) {
        if (s.isEmpty())
            return s;
        try {
            return new String(Base64.decode(s.getBytes("UTF-8"), Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {

        }
        return s;
    }

    public static String base64UrlEncode(String simple) {
        try {
            String s = new String(Base64.encode(simple.getBytes("UTF-8"), Base64.NO_WRAP)); // Regular base64 encoder
            s = s.split("=")[0]; // Remove any trailing '='s
            s = s.replace('+', '-'); // 62nd char of encoding
            s = s.replace('/', '_'); // 63rd char of encoding
            return s;
        } catch (UnsupportedEncodingException e) {

        }

        return simple;
    }

    public static String base64UrlDecode(String cipher) {
        String s = cipher;
        s = s.replace('-', '+'); // 62nd char of encoding
        s = s.replace('_', '/'); // 63rd char of encoding
        switch (s.length() % 4) { // Pad with trailing '='s
            case 0:
                break; // No pad chars in this case
            case 2:
                s += "==";
                break; // Two pad chars
            case 3:
                s += "=";
                break; // One pad char
            default:
                break;
        }
        try {
            return new String(Base64.decode(s.getBytes("UTF-8"), Base64.NO_WRAP)); // Standard base64 decoder
        } catch (UnsupportedEncodingException e) {

        }

        return cipher;
    }

    public static String md5encode(String originString) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] btInput = originString.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toUrlEncode(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getDeviceInfo() {
        return android.os.Build.MODEL + ";" + android.os.Build.VERSION.SDK
                + ";" + android.os.Build.VERSION.RELEASE;
    }

    public static String getLocale(Context context) {
        try {
            return context.getResources().getConfiguration().locale
                    .getLanguage();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "en";
    }

    public static synchronized String getDeviceId(Context context) {
        SharedPreferences settings = context.getSharedPreferences("device_id", Context.MODE_PRIVATE);
        String id = settings.getString("ID", "");
        if (id.isEmpty()) {
            int len = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.SUPPORTED_ABIS.length > 0)
                    len = Build.SUPPORTED_ABIS[0].length();
                else
                    len = Build.CPU_ABI.length();
            }
            else
                len = Build.CPU_ABI.length();

            String deviceShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) +
                    (len % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) +
                    (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

            String serial = null;
            try {
                serial = Build.class.getField("SERIAL").get(null).toString();
            } catch (Exception e) {
                e.printStackTrace();
                serial = "seastarserial";
            }

            id = new UUID(deviceShort.hashCode(), serial.hashCode()).toString();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("ID", id);
            editor.commit();
        }

        return id;
    }

    public static void showToast(final Activity activity, final int imgResId, final String content, final int duration) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = activity.getLayoutInflater().inflate(com.seastar.R.layout.layout_toast, null);

                ImageView image = (ImageView) view.findViewById(R.id.toast_image);
                image.setImageResource(imgResId);

                TextView tv = (TextView) view.findViewById(R.id.toast_text);
                tv.setText(content);

                final Toast toast = new Toast(activity.getApplicationContext());
                toast.setView(view);
                toast.setDuration(duration);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 20);
                toast.show();
            }
        });
    }

    public static void showDialog(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(message);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    public static ProgressDialog showProgress(Activity activity) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

        return dialog;
    }

    public static void hideProgress(Activity activity, final ProgressDialog dialog) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    //<uses-permission android:name="android.permission.READ_PHONE_STATE" />
    //GSM手机的 IMEI 和 CDMA手机的 MEID
    public static String getImei(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return "";
            return tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getImsi(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return "";
            return tm.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    //<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    public static String getMacAddress(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                WifiInfo wifiInfo = wm.getConnectionInfo();
                if (wifiInfo != null)
                    return wifiInfo.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getBoard() {
        return Build.BOARD;
    }

    public static String getDevice() {
        return Build.DEVICE;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getProduct() {
        return Build.PRODUCT;
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static String getNetworkType(Context context) {
        String strNetworkType = "";

        try {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();

                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = "4G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = "3G";
                            } else {
                                strNetworkType = _strSubTypeName;
                            }

                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strNetworkType;
    }

}
