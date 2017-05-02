package com.seastar.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.seastar.config.Config;
import com.seastar.listener.OnActionFinishListener;

/**
 * Created by osx on 17/2/27.
 */

public class GoogleApiClientHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static GoogleApiClientHelper instance = new GoogleApiClientHelper();

    private GoogleApiClient googleApiClient;
    private OnActionFinishListener currentActionFinishListener;

    public static GoogleApiClientHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        if (isAvailable(context)) {
            // 不是FragmentActivity需要自己管理apiClient的lifecircule
            // 手动管理时需要自己在合适的地点调用connect和disconnect，在activity中推荐onStart和onStop中调用
            // 使用Drive_API和Games_API时可能失败，因为没有帐号登录app
            Games.GamesOptions gamesOptions = Games.GamesOptions.builder().setShowConnectingPopup(false).build();
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Games.API, gamesOptions)
                    .addScope(Games.SCOPE_GAMES)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
        }
    }

    public void setConnectActionListener(OnActionFinishListener listener) {
        currentActionFinishListener = listener;
    }

    public void connect() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    public void disconnect() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            //Games.signOut(googleApiClient);
        }
    }

    public boolean isConnected() {
        if (googleApiClient != null) {
            return googleApiClient.isConnected();
        } else {
            return false;
        }
    }

    public Player getPlayer() {
        if (googleApiClient != null) {
            return Games.Players.getCurrentPlayer(googleApiClient);
        } else {
            return null;
        }
    }

    // 解锁成就
    public void unlockAchievement(String achievementId) {
        if (googleApiClient == null) return;

        if (googleApiClient.isConnected() && googleApiClient.hasConnectedApi(Games.API)) {
            Games.Achievements.unlock(googleApiClient, achievementId);
        }
    }

    // 添加成就
    public void incrementAchievement(String achievementId, int score) {
        if (googleApiClient == null) return;

        if (googleApiClient.isConnected() && googleApiClient.hasConnectedApi(Games.API)) {
            Games.Achievements.increment(googleApiClient, achievementId, score);
        }
    }

    // 展示成就
    public void showAchievement(Activity activity) {
        if (googleApiClient == null) return;

        if (googleApiClient.isConnected() && googleApiClient.hasConnectedApi(Games.API)) {
            Log.d("Seastar", "show achievement");
            activity.startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClient), 500001);
        }
    }

    // 排行榜加分
    public void updateScoreOnLeaderboard(String leaderboardId, int score) {
        if (googleApiClient == null) return;

        if (googleApiClient.isConnected() && googleApiClient.hasConnectedApi(Games.API)) {
            Games.Leaderboards.submitScore(googleApiClient, leaderboardId, score);
        }
    }

    // 展示排行榜
    public void showLeaderboard(Activity activity) {
        if (googleApiClient == null) return;

        if (googleApiClient.isConnected() && googleApiClient.hasConnectedApi(Games.API)) {
            Log.d("Seastar", "show leaderboard");
            activity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(googleApiClient), 500002);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(Config.TAG, "GoogleApiClientHelper::onConnected: success");
        if (currentActionFinishListener != null) {
            currentActionFinishListener.onFinished(true);
            currentActionFinishListener = null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Config.TAG, "GoogleApiClientHelper::onConnectionSuspended: reconnect");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Seastar", "google login fail");
        if (connectionResult != null) {
            Log.d("Seastar", "result: " + connectionResult.toString() + " " + connectionResult.getErrorCode());
        }
        if (currentActionFinishListener != null) {
            currentActionFinishListener.onFinished(false);
            currentActionFinishListener = null;
        }
    }

    public boolean isAvailable(Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }
}
