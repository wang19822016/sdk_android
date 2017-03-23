package com.seastar.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.seastar.config.Config;
import com.seastar.listener.OnActionFinishListener;
import com.seastar.listener.OnActionTwoFinishListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by osx on 17/2/27.
 */
/*
Login权限：
 默认公开权限是：public_profile email user_friends
 public_profile权限内容（允许访问用户公开档案的各版块中所列的信息）：
    id
    name
    first_name
    last_name
    age_range
    link
    gender(访问限制：查询的用户是应用用户；查询的用户正在使用应用，且是应用用户的好友；查询的用户正在使用应用，虽然他并非应用用户的好友，但应用在调用中包含应用访问口令或者带有 appsecret_proof 参数)
    locale(访问限制：查询的用户是应用用户；查询的用户正在使用应用，且是应用用户的好友；查询的用户正在使用应用，虽然他并非应用用户的好友，但应用在调用中包含应用访问口令或者带有 appsecret_proof 参数)
    picture
    timezone(查询的用户即提出请求的用户可以访问)
    updated_time(查询的用户即提出请求的用户可以访问)
    verified

 user_friends权限内容（允许访问也在使用您的应用的好友清单。这些好友可在用户对象的好友连线找到）：
    注：要让某用户显示在另外一名用户的好友列表中，这两名用户都必须同意与您的应用分享他们的好友列表，且未在登录流程中拒绝授予该权限。此外，还必须在登录流程中向这两名用户请求授予 user_friends 权限。


其他权限说明：
 publish_actions：允许代表应用用户发布帖子、开放图谱操作、成就、分数和其他活动。因为该权限可让您代表用户发布内容，所以请阅读开放平台政策，确保自己理解如何正确使用该权限。
 动态发布对话框、请求对话框或“发送”对话框不需要此权限。
 “发送”对话框：用户可使用“发送”对话框向特定好友私密发送内容。他们可以选择将链接作为 Facebook 消息私密分享。
 请求对话框：游戏请求为玩家提供邀请好友玩游戏的机制。玩家可以向一个或多个好友发送请求，请求应始终包含游戏行动号召按钮。接收方可以是现有玩家，也可以是新玩家。
 动态发布对话框：在应用中添加动态发布对话框后，可方便用户将单条动态发布到自己的时间线。通过这种方式发布的内容包括应用管理的说明和内容分享者的个人评论。

 publish_pages：能以用户所管理的任何主页的身份发帖、评论和赞。

 user_posts：允许访问用户的时间线帖子。

 user_photos：允许访问用户上传的或圈出该用户的照片。


图谱API：
 获取个人主页信息，提供社交元素。
 获取用户信息，例如赞或照片。
 发布帖子（包括视频或照片）到 Facebook。
 发布开放图谱动态到 Facebook。


 FBSDKShareDialog不用申请权限，分享的内容也可能被fb修改。FBSDKShareAPI需要申请publish_actions，不会被修改内容。

分享内容有三种；
 以链接分享的形式分享代表游戏内容（例如：级别、成就等）的轻量级动态。
 分享照片和视频媒体，例如：玩家基地的截图或视频回放。
 发布内容丰富且架构完善的自定义动态，例如：关于完成某个游戏关卡或赢得游戏战斗的动态。
实施分享的方式有二种：
 1.对话框分享(使用fb提供的默认对话框分享)：
 分享对话框适用于网页、iOS 和 Android，让用户能够使用与 Facebook 一致的用户界面分享内容。此外，使用分享对话框还无需实施 Facebook 登录，这让开发者的实施过程变得更简单。
 2.图谱API 分享（需要自己开发分享内容展示窗口，需要开启图谱api设置，需要审核权限）：
 图谱 API 可用于分享链接和自定义开放图谱动态。这种方法还适用于所有平台。使用此 API，您就可以构建自己的用户界面，从而更有力地控制分享体验，但使用此 API 还必须实施 Facebook 登录。
链接分享:
 1.为确保链接在动态消息中展示效果良好，您可以使用开放图谱 (OG) 元数据优化链接。通过 OG 元数据，您可以确定主图片、标题和说明，Facebook 在动态消息中展示链接时会使用到这些内容。
 2.链接目的地。移动平台链接目的地最好设置成应用商店的链接。
照片和视频分享；
 1.可以直接使用图片视频分享，优势：相关内容会在动态消息中以完整的尺寸显示、视频内容会在电脑和移动设备上自动播放；劣势：由于照片/视频体验是针对查看内容而优化的，所以这类动态不会直接吸引太多玩家前往您的游戏。
 2.可以用链接分享。向链接的元数据添加开放图谱视频标签，Facebook 就会在动态消息中显示视频内容。视频不会自动播放。
extra:
 1.如果有特殊分享需求，必须使用图谱API来分享。
 2.分享链接OG设置参考：https://developers.facebook.com/docs/sharing/webmasters。
 3.图谱API使用参考：https://developers.facebook.com/docs/sharing/opengraph。



游戏请求:
 1.游戏请求为玩家提供邀请好友玩游戏的机制。玩家可以向一个或多个好友发送请求，请求应始终包含游戏行动号召按钮。接收方可以是现有玩家，也可以是新玩家。
   游戏请求可用于吸引新玩家或重新吸引现有玩家。请求可在两种情况下发送：
   接收方是发送方的好友且尚未验证游戏。这种情况适合使用邀请。
   接收方是发送方的好友且之前已验证游戏。这种情况适合使用回合制游戏通知、赠送礼物和寻求帮助。
   请求会在fb应用和fb页面内显示，可以自己定制请求页面。
 2.在游戏启动时应该使用图谱api获取所有请求，然后删除请求
*/
public class FacebookHelper {
    private CallbackManager currentCallbackManager;
    private OnActionFinishListener currentOnActionCallback;
    private String nextPos;
    private String prevPos;

    private static FacebookHelper instance = new FacebookHelper();
    public static FacebookHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        FacebookSdk.sdkInitialize(context, new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
            }
        });

        currentCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(currentCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(Config.TAG, "Facebook::login login success!");
                if (currentOnActionCallback != null) {
                    currentOnActionCallback.onFinished(true);
                    currentOnActionCallback = null;
                }
            }

            @Override
            public void onCancel() {
                Log.d(Config.TAG, "Facebook::login login cancel!");
                if (currentOnActionCallback != null) {
                    currentOnActionCallback.onFinished(false);
                    currentOnActionCallback = null;
                }
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(Config.TAG, "Facebook::login login error! : " + e.toString());
                if (currentOnActionCallback != null) {
                    currentOnActionCallback.onFinished(false);
                    currentOnActionCallback = null;
                }
            }
        });
    }


    public void onResume(Activity activity) {
        AppEventsLogger.activateApp(activity);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentCallbackManager != null)
            currentCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void login(Activity activity, OnActionFinishListener listener) {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null && !token.isExpired()) {
            if (listener != null) {
                listener.onFinished(true);
            }
            return;
        }

        currentOnActionCallback = listener;
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_friends"));
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        AccessToken.setCurrentAccessToken(null);
    }

    public AccessToken getAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }

    // 照片大小必须小于 12MB
    // 用户需要安装版本 7.0 或以上的原生 iOS 版 Facebook 应用
    public void share(Activity activity, String imageUri, String caption, final OnActionFinishListener listener) {

        List<SharePhoto> photos = new ArrayList<SharePhoto>();
        SharePhoto photo = new SharePhoto.Builder().setImageUrl(Uri.parse(imageUri)).setCaption(caption).build();
        photos.add(photo);

        SharePhotoContent.Builder builder = new SharePhotoContent.Builder();
        builder.addPhotos(photos);
        SharePhotoContent sharePhotoContent = builder.build();

        FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(Config.TAG, "Facebook::sharePhotoByUri facebook分享成功, 结果:" + result);
                if (listener != null) {
                    listener.onFinished(true);
                }
            }

            @Override
            public void onCancel() {
                Log.d(Config.TAG, "Facebook::sharePhotoByUri facebook取消分享");
                if (listener != null) {
                    listener.onFinished(false);
                }
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::sharePhotoByUri facebook分享失败，失败信息:" + error);
                if (listener != null) {
                    listener.onFinished(false);
                }
            }
        };

        // 进行分享
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            // 使用apps分享
            ShareDialog shareDialog = new ShareDialog(activity);
            shareDialog.registerCallback(currentCallbackManager, shareCallback);
            shareDialog.show(sharePhotoContent);
        } else {
            if (listener != null) {
                listener.onFinished(false);
            }
        }
    }

    // bitmap 图片 （照片必须小于12MB大小并且需要安装版本7或更高版本的应用程序）
    // caption 图片标题
    public void share(Activity activity, Bitmap bitmap, String caption, final OnActionFinishListener listener) {

        List<SharePhoto> photos = new ArrayList<SharePhoto>();
        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).setCaption(caption).build();
        photos.add(photo);

        SharePhotoContent.Builder builder = new SharePhotoContent.Builder();
        builder.addPhotos(photos);
        SharePhotoContent sharePhotoContent = builder.build();

        FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(Config.TAG, "Facebook::sharePhoto facebook分享成功, 结果:" + result);
                if (listener != null) {
                    listener.onFinished(true);
                }
            }

            @Override
            public void onCancel() {
                Log.d(Config.TAG, "Facebook::sharePhoto facebook取消分享");
                if (listener != null) {
                    listener.onFinished(false);
                }
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::sharePhoto facebook分享失败，失败信息:" + error);
                if (listener != null) {
                    listener.onFinished(false);
                }
            }
        };

        // 进行分享
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            // 使用apps分享
            ShareDialog shareDialog = new ShareDialog(activity);
            shareDialog.registerCallback(currentCallbackManager, shareCallback);
            shareDialog.show(sharePhotoContent);
        } else {
            if (listener != null) {
                listener.onFinished(false);
            }
        }
    }


    // contentURL要分享的链接
    // contentTitle表示链接中的内容的标题
    // imageURL 在帖子中显示的缩略图的网址
    // contentDescription内容描述
    // 注意：如果分享的是iTunes或GooglePlay商店链接，不会发布分享中指定的任何图片和说明，而是通过网络爬虫直接从商店爬到的应用信息。
    public void share(Activity activity, String contentURL, String contentTitle, String imageURL, String contentDescription,
                      final OnActionFinishListener listener) {

        ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
        builder.setContentUrl(Uri.parse(contentURL));
        builder.setContentTitle(contentTitle);
        builder.setImageUrl(Uri.parse(imageURL));
        builder.setContentDescription(contentDescription);

        ShareLinkContent content = builder.build();

        FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(Config.TAG, "Facebook::shareUrl facebook分享成功, 结果:" + result);
                if (listener != null) {
                    listener.onFinished(true);
                }
            }

            @Override
            public void onCancel() {
                Log.d(Config.TAG, "Facebook::shareUrl facebook取消分享");
                if (listener != null) {
                    listener.onFinished(false);
                }
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::shareUrl facebook分享失败，失败信息:" + error);
                if (listener != null) {
                    listener.onFinished(false);
                }
            }
        };

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareDialog shareDialog = new ShareDialog(activity);
            shareDialog.registerCallback(currentCallbackManager, shareCallback);
            shareDialog.show(content);
        } else {
            if (listener != null) {
                listener.onFinished(false);
            }
        }
    }

    // 使用 iOS SDK 提供的好友选择工具启动请求对话框
    public void doGameRequest(Activity activity, String title, String message, final OnActionFinishListener listener) {

        GameRequestDialog shareGameDialog = new GameRequestDialog(activity);
        GameRequestContent content = new GameRequestContent.Builder().setMessage(message).setTitle(title).build();
        shareGameDialog.registerCallback(currentCallbackManager, new FacebookCallback<GameRequestDialog.Result>() {

            @Override
            public void onSuccess(GameRequestDialog.Result result) {
                Log.d(Config.TAG, "Facebook::doGameRequest facebook分享成功, 结果:" + result);
                if (listener != null) {
                    listener.onFinished(true);
                }
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::doGameRequest facebook分享失败，失败信息:" + error);
                if (listener != null) {
                    listener.onFinished(false);
                }
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::doGameRequest facebook取消分享");
                if (listener != null) {
                    listener.onFinished(false);
                }
            }
        });

        if (shareGameDialog.canShow(content)) {
            shareGameDialog.show(content);
        } else {
            if (listener != null) {
                listener.onFinished(false);
            }
        }
    }

    /*
     {
     "data": [
     {
     "id": "REQUEST_OBJECT_ID",
     "application": {
     "name": "APP_DISPLAY_NAME",
     "namespace": "APP_NAMESPACE",
     "id": "APP_ID"
     },
     "to": {
     "name": "RECIPIENT_FULL_NAME",
     "id": "RECIPIENT_USER_ID"
     },
     "from": {
     "name": "SENDER_FULL_NAME",
     "id": "SENDER_USER_ID"
     },
     "message": "ATTACHED_MESSAGE",
     "created_time": "2014-01-17T16:39:00+0000"
     }
     ]
     }
    */
    // 读取接收方所有请求
    public void deleteGameRequest() {
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(), "me/apprequests");
            request.setCallback(new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    if (graphResponse.getJSONObject() != null) {
                        JSONObject js = graphResponse.getJSONObject();
                        try {
                            JSONArray jsArray = js.getJSONArray("data");
                            for (int i = 0; i < jsArray.length(); i++) {
                                JSONObject jsObject = jsArray.getJSONObject(i);
                                String inviteId = jsObject.getString("id");
                                GraphRequest delRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), inviteId + "_" + AccessToken.getCurrentAccessToken().getUserId());
                                delRequest.executeAsync();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            request.executeAsync();
        }
    }

    // 邀请好友
    public void doAppInvite(Activity activity, String applinkUrl, String previewImageUrl, final OnActionFinishListener listener) {
        AppInviteContent appInviteContent = new AppInviteContent.Builder().setApplinkUrl(applinkUrl)
                .setPreviewImageUrl(previewImageUrl).build();

        AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
        appInviteDialog.show(appInviteContent);
        appInviteDialog.registerCallback(currentCallbackManager, new FacebookCallback<AppInviteDialog.Result>() {
            @Override
            public void onSuccess(AppInviteDialog.Result result) {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::inviteFriends facebook邀请好友成功, 结果：" + result.toString());
                if (listener != null) {
                    listener.onFinished(true);
                }
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::inviteFriends facebook邀请好友失败, 错误信息：" + error.getMessage());
                if (listener != null) {
                    listener.onFinished(false);
                }
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Log.d(Config.TAG, "Facebook::inviteFriends facebook取消好友邀请");
                if (listener != null) {
                    listener.onFinished(false);
                }
            }
        });
    }

    public void getFriendInfo(String height, String width, final String limit, final OnActionTwoFinishListener listener) {
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
                "me/taggable_friends", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.d(Config.TAG,
                                "Facebook::requestAllFriends facebook获取用户好友的信息------response:" + response.toString());

                        parseAllFriendsInfo(response.getJSONObject().toString());
                        if (listener != null) {
                            listener.onFinished(true, response.getJSONObject().toString());
                        }
                    }
                });
        String field[] = { "id", "name", "picture.height(" + height + ").width(" + width + ")" };
        Bundle parameter = graphRequest.getParameters();
        if (limit != null) {
            parameter.putString("limit", limit);
        }
        parameter.putString("fields", TextUtils.join(",", field));
        graphRequest.setParameters(parameter);
        Log.d(Config.TAG, graphRequest.toString());
        graphRequest.executeAsync();
    }

    // 获取下一步分页信息
    public void getNextFriendInfo(final OnActionTwoFinishListener listener) {
        if (nextPos == null)
            return;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(nextPos).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(Call arg0, Response response) throws IOException {
                // TODO 自动生成的方法存根
                String responseCall = response.body().string();
                Log.d(Config.TAG, responseCall);
                parseAllFriendsInfo(responseCall);
                if (listener != null) {
                    listener.onFinished(true, responseCall);
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO 自动生成的方法存根
                if (listener != null) {
                    listener.onFinished(false, "");
                }
            }
        });

    }

    // 获取上一步分页信息
    public void getPrevFriendInfo(final OnActionTwoFinishListener listener) {
        if (prevPos == null)
            return;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(prevPos).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(Call arg0, Response response) throws IOException {
                // TODO 自动生成的方法存根
                String responseCall = response.body().string();
                Log.d(Config.TAG, responseCall);
                parseAllFriendsInfo(responseCall);
                if (listener != null) {
                    listener.onFinished(true, responseCall);
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO 自动生成的方法存根
                if (listener != null) {
                    listener.onFinished(false, "");
                }
            }
        });
    }

    /**
     * 获取用户自己的信息
     */
    public void getMeInfo(final OnActionTwoFinishListener listener) {
        if (AccessToken.getCurrentAccessToken() == null) {
            Log.d(Config.TAG, "Facebook::getMyInfo facebook获取自己的信息失败，facebook没有登录");
            if (listener != null) {
                listener.onFinished(false, "");
            }
            return;
        }
		/*-----------------------------------获取用户的信息-------------------------------------------*/
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        Log.d(Config.TAG, "Facebook::getMyInfo facebook获取用户的信息 :" + object.toString());
                        if (response.getJSONObject() != null) {
                            String jsonStr = "";
                            try {
                                JSONObject json = response.getJSONObject();
                                jsonStr = json.toString();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (listener != null) {
                                listener.onFinished(true, jsonStr);
                            }
                        } else {
                            if (listener != null) {
                                listener.onFinished(false, "");
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");//,email,first_name,last_name,middle_name,name_format,third_party_id,gender,location,friends");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void parseAllFriendsInfo(String response) {
        try {
            JSONObject obj = null;
            obj = new JSONObject(response);
            JSONArray data = null;
            if (obj.has("data")) {
                data = obj.getJSONArray("data");
            }
            String paging = "";
            if (obj.has("paging")) {
                paging = obj.getString("paging");
            }
            JSONObject pagingObj = null;
            if (paging != "") {
                pagingObj = new JSONObject(paging.toString());
            }
            if (data != null && data.length() > 0) {
                if (pagingObj.has("next")) {
                    nextPos = pagingObj.getString("next");
                } else {
                    nextPos = null;
                }
                if (pagingObj.has("previous")) {
                    prevPos = pagingObj.getString("previous");
                } else {
                    prevPos = null;
                }
            } else {
                nextPos = null;
                prevPos = null;
            }
            Log.d(Config.TAG, "nextStr------response:" + nextPos);
            Log.d(Config.TAG, "previous------response:" + prevPos);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
