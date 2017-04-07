package com.seastar.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.seastar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by osx on 17/3/31.
 */

public class FacebookSocialActivity extends BaseActivity {

    private CallbackManager callbackManager;
    private ArrayList<GridItem> inviteData = null;
    private GridViewAdapter gridViewAdapter = null;

    private String bindUrl = "https://www.facebook.com";
    private String mainPageUrl = "https://www.facebook.com";
    private String shareUrl = "https://www.facebook.com";
    private String shareImageUrl = "https://www.facebook.com";
    private String shareTitle = "街机三国";
    private String shareDescription = "街机三国";
    private String likeUrl = "https://www.facebook.com";
    private String inviteTitle = "邀请";
    private String inviteMessage = "快來玩街機三國";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_social);

        Intent intent = getIntent();
        bindUrl = intent.getStringExtra("bindUrl");
        mainPageUrl = intent.getStringExtra("mainPageUrl");
        shareUrl = intent.getStringExtra("shareUrl");
        shareImageUrl = intent.getStringExtra("shareImageUrl");
        shareTitle = intent.getStringExtra("shareTitle");
        shareDescription = intent.getStringExtra("shareDescription");
        likeUrl = intent.getStringExtra("likeUrl");
        inviteTitle = intent.getStringExtra("inviteTitle");
        inviteMessage = intent.getStringExtra("inviteMessage");

        initSelector();
        initFacebook();
        initShare();
        //initLike();
        initProfile();

        initGridView();
        initClose();
        initSend();
        initCheckbox();


        checkFacebookLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initSelector() {
        final ImageView inviteImg = (ImageView) findViewById(R.id.facebook_social_invite_img);
        final TextView inviteTxt = (TextView) findViewById(R.id.facebook_social_invite_text);
        final ImageView inviteArrow = (ImageView) findViewById(R.id.facebook_social_invite_arrow);

        final ImageView bindImg = (ImageView) findViewById(R.id.facebook_social_bind_img);
        final TextView bindTxt = (TextView) findViewById(R.id.facebook_social_bind_text);
        final ImageView bindArrow = (ImageView) findViewById(R.id.facebook_social_bind_arrow);

        final ImageView gameImg = (ImageView) findViewById(R.id.facebook_social_game_img);
        final TextView gameTxt = (TextView) findViewById(R.id.facebook_social_game_text);
        final ImageView gameArrow = (ImageView) findViewById(R.id.facebook_social_game_arrow);

        final View shareView = findViewById(R.id.facebook_social_right_share);
        final View bindView = findViewById(R.id.facebook_social_right_bind);

        final TextView bindUrlTxt = (TextView) findViewById(R.id.facebook_social_bind_url);
        Button copyBtn = (Button) findViewById(R.id.facebook_social_bind_copy);
        bindUrlTxt.setText(bindUrl);

        shareView.setVisibility(View.VISIBLE);
        bindView.setVisibility(View.GONE);

        inviteImg.setImageResource(R.drawable.fb_social_invite_light);
        inviteTxt.setTextColor(Color.parseColor("#4267B2"));
        bindArrow.setVisibility(View.INVISIBLE);
        gameArrow.setVisibility(View.INVISIBLE);

        inviteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteImg.setImageResource(R.drawable.fb_social_invite_light);
                inviteTxt.setTextColor(Color.parseColor("#4267B2"));
                inviteArrow.setVisibility(View.VISIBLE);

                bindImg.setImageResource(R.drawable.fb_social_chain_black);
                bindTxt.setTextColor(Color.parseColor("#000000"));
                bindArrow.setVisibility(View.INVISIBLE);

                gameImg.setImageResource(R.drawable.fb_social_up_black);
                gameTxt.setTextColor(Color.parseColor("#000000"));
                gameArrow.setVisibility(View.INVISIBLE);

                shareView.setVisibility(View.VISIBLE);
                bindView.setVisibility(View.GONE);
            }
        });

        bindTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteImg.setImageResource(R.drawable.fb_social_invite_black);
                inviteTxt.setTextColor(Color.parseColor("#000000"));
                inviteArrow.setVisibility(View.INVISIBLE);

                bindImg.setImageResource(R.drawable.fb_social_chain_light);
                bindTxt.setTextColor(Color.parseColor("#4267B2"));
                bindArrow.setVisibility(View.VISIBLE);

                gameImg.setImageResource(R.drawable.fb_social_up_black);
                gameTxt.setTextColor(Color.parseColor("#000000"));
                gameArrow.setVisibility(View.INVISIBLE);

                shareView.setVisibility(View.GONE);
                bindView.setVisibility(View.VISIBLE);
            }
        });

        gameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteImg.setImageResource(R.drawable.fb_social_invite_black);
                inviteTxt.setTextColor(Color.parseColor("#000000"));
                inviteArrow.setVisibility(View.INVISIBLE);

                bindImg.setImageResource(R.drawable.fb_social_chain_black);
                bindTxt.setTextColor(Color.parseColor("#000000"));
                bindArrow.setVisibility(View.INVISIBLE);

                gameImg.setImageResource(R.drawable.fb_social_up_light);
                gameTxt.setTextColor(Color.parseColor("#4267B2"));
                gameArrow.setVisibility(View.VISIBLE);

                shareView.setVisibility(View.GONE);
                bindView.setVisibility(View.GONE);

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
                    intent.setData(Uri.parse(mainPageUrl)); //为Intent设置DATA属性
                     startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("invite", bindUrl);
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(FacebookSocialActivity.this, "復制成功，可以發給朋友們了。", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                initProfile();

                requestFacebookFriendsInfo();
            }

            @Override
            public void onCancel() {
                Log.d("Seastar", "fb login cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("Seastar", e.getLocalizedMessage() + "  " + e.getMessage());
            }
        });
    }

    private void initShare() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(shareUrl))
                .setImageUrl(Uri.parse(shareImageUrl))
                .setContentTitle(shareTitle)
                .setContentDescription(shareDescription)
                .build();

        ShareButton shareButton = (ShareButton)findViewById(R.id.facebook_social_share);
        shareButton.setShareContent(content);
    }

    private void initLike() {
        LikeView likeView = (LikeView) findViewById(R.id.facebook_social_like);
        likeView.setObjectIdAndType(
                likeUrl,
                LikeView.ObjectType.PAGE);
    }

    private void initProfile() {
        ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.facebook_social_user_avtar);
        if (AccessToken.getCurrentAccessToken() != null) {
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                profilePictureView.setProfileId(profile.getId());

                TextView textView = (TextView) findViewById(R.id.facebook_social_user_name);
                textView.setText(profile.getName());
            }
        }
    }

    private void initGridView() {
        GridView gridView = (GridView) findViewById(R.id.facebook_social_grid);

        inviteData = new ArrayList<GridItem>();

        gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item, inviteData);

        gridView.setAdapter(gridViewAdapter);
    }

    private void initCheckbox() {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.facebook_social_all);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < inviteData.size(); i++) {
                    inviteData.get(i).setChecked(checkBox.isChecked());
                }

                gridViewAdapter.setGridData(inviteData);
            }
        });
    }

    private void checkFacebookLogin() {
        if (AccessToken.getCurrentAccessToken() == null ||
                !AccessToken.getCurrentAccessToken().getPermissions().contains("user_friends")) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile,user_friends"));
        } else {
            requestFacebookFriendsInfo();
        }
    }

    private void initClose() {
        ImageView button = (ImageView) findViewById(R.id.facebook_social_close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSocialActivity.this.finish();
            }
        });
    }

    private void initSend() {
        Button button = (Button) findViewById(R.id.facebook_social_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder = new StringBuilder();

                /*
                for (int i = 0; i < inviteData.size(); i++) {
                    Log.d("Seastar", "ID: " + inviteData.get(i).getId() + ", Name: " + inviteData.get(i).getName() + ", Select: " + inviteData.get(i).isChecked());
                    if (inviteData.get(i).isChecked()) {
                        builder.append(inviteData.get(i).getId());
                        builder.append(",");
                    }
                }

                if (builder.length() > 0) {
                    inviteFacebookFriends(builder.substring(0, builder.length() - 1));
                }
                inviteFacebookFriends("a");
                */

                List<String> recepits = new ArrayList<String>();
                for (int i = 0; i < inviteData.size(); i++) {
                    if (inviteData.get(i).isChecked()) {
                        recepits.add(inviteData.get(i).getId());
                    }
                }

                inviteFacebookFriends(recepits);
            }
        });
    }

    private void requestFacebookFriendsInfo() {
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/invitable_friends", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() != null)
                            Log.d("Seastar", response.getError().getErrorMessage() + " " + response.getError().getErrorUserMessage());

                        try {
                            JSONObject json = response.getJSONObject();
                            if (json != null) {
                                Log.d("Seastar", "friends: " + json.toString());

                                inviteData.clear();

                                JSONArray jsonArray = json.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject friend = jsonArray.getJSONObject(i);
                                    if (friend != null) {
                                        String id = friend.getString("id");
                                        String name = friend.getString("name");
                                        String url = "";
                                        JSONObject picture = friend.getJSONObject("picture");
                                        if (picture != null) {
                                            JSONObject pictureData = picture.getJSONObject("data");
                                            if (pictureData != null) {
                                                url = pictureData.getString("url");
                                            }
                                        }

                                        GridItem item = new GridItem();
                                        if (id != null)
                                            item.setId(id);
                                        if (name != null)
                                            item.setName(name);
                                        if (url != null)
                                            item.setAvatar(url);

                                        inviteData.add(item);
                                    }
                                }

                                gridViewAdapter.setGridData(inviteData);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        String field[] = { "id", "name", "picture" };
        Bundle parameter = graphRequest.getParameters();
        parameter.putString("limit", "50");
        parameter.putString("fields", TextUtils.join(",", field));
        graphRequest.setParameters(parameter);
        graphRequest.executeAsync();
    }

    private void inviteFacebookFriends(List<String> recipients) {
        GameRequestDialog dialog = new GameRequestDialog(this);
        GameRequestContent content = new GameRequestContent.Builder()
                .setMessage(inviteMessage)
                .setTitle(inviteTitle)
                .setRecipients(recipients)
                //.setActionType(GameRequestContent.ActionType.SEND)
                //.setObjectId(UUID.randomUUID().toString())
                .build();
        dialog.registerCallback(callbackManager, new FacebookCallback<GameRequestDialog.Result>() {
            @Override
            public void onSuccess(GameRequestDialog.Result result) {
                String id = result.getRequestId();
                List<String> recipients = result.getRequestRecipients();
                Log.d("Seastar", "invite success: id=" + id);
                FacebookSocialActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FacebookSocialActivity.this, "邀請成功，快告訴朋友們去邦定領取獎勵吧。", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onCancel() {
                Log.d("Seastar", "invite cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("Seastar", "invite error: " + e.toString());
            }
        });
        if (dialog.canShow(content))
            dialog.show(content);
    }

    public class GridItem {
        String id = "";
        String avatar = "";
        String name = "";
        boolean checked = false;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    public class GridViewAdapter extends ArrayAdapter<GridItem> {
        private int layoutResourceId;
        private ArrayList<GridItem> gridData = new ArrayList<GridItem>();

        public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects) {
            super(context, resource, objects);
            gridData = objects;
            layoutResourceId = resource;
        }

        public void setGridData(ArrayList<GridItem> mGridData) {
            this.gridData = mGridData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(layoutResourceId, parent, false);

                holder = new ViewHolder();
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.grid_item_check);
                holder.imageView = (ImageView) convertView.findViewById(R.id.grid_item_img);
                holder.textView = (TextView) convertView.findViewById(R.id.grid_item_text);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final GridItem item = gridData.get(position);
            holder.textView.setText(item.name);
            holder.checkBox.setChecked(item.checked);
            holder.imageView.setImageResource(0);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setChecked(holder.checkBox.isChecked());
                }
            });

            //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑
            new AsyncTask<String, Void, Bitmap>()
            {
                @Override
                protected Bitmap doInBackground(String... params)
                {
                    String imageUrl = params[0];
                    try
                    {
                        URL url = new URL(imageUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        return bitmap;
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap result)
                {
                    holder.imageView.setImageBitmap (result);
                    super.onPostExecute(result);
                }
            }.execute(item.avatar);

            return convertView;
        }

        private class ViewHolder {
            public TextView textView;
            public ImageView imageView;
            public CheckBox checkBox;
        }
    }
}
