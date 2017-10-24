package com.link184.respiration.subscribers;


import java.util.Map;

public abstract class SingleListSubscriberFirebase<T> extends ListSubscriberFirebase<T>{
    @Override
    public final void onSuccess(Map<String, T> dataSnapShot) {
        super.onSuccess(dataSnapShot);
        dispose();
    }
}
