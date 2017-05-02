package com.seastar.listener;

/**
 * Created by osx on 17/3/16.
 */

public interface NetworkListener {
    void onFailure();
    void onSuccess(int code, String body);
}
