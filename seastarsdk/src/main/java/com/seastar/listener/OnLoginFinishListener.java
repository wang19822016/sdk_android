package com.seastar.listener;

/**
 * Created by osx on 16/12/19.
 */

public interface OnLoginFinishListener {
    void onFinished(boolean bool, String userId, String session);
}
