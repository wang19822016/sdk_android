package com.seastar.listener;


/**
 * Created by osx on 17/3/18.
 */

public class ListenerMgr {
    private static ListenerMgr instance = new ListenerMgr();
    private OnLoginFinishListener loginFinishListener;
    private OnPurchaseFinishListener purchaseFinishListener;
    private OnActionFinishListener sharedFinishListener;

    public static ListenerMgr getInstance() {
        return instance;
    }

    public OnLoginFinishListener getLoginFinishListener() {
        return loginFinishListener;
    }

    public void setLoginFinishListener(OnLoginFinishListener loginFinishListener) {
        this.loginFinishListener = loginFinishListener;
    }

    public OnPurchaseFinishListener getPurchaseFinishListener() {
        return purchaseFinishListener;
    }

    public void setPurchaseFinishListener(OnPurchaseFinishListener purchaseFinishListener) {
        this.purchaseFinishListener = purchaseFinishListener;
    }

    public void setSharedFinishListener(OnActionFinishListener sharedFinishListener) {
        this.sharedFinishListener = sharedFinishListener;
    }

    public OnActionFinishListener getSharedFinishListener() {
        return sharedFinishListener;
    }
}
