package com.link184.respiration.subscribers;

import java.util.Map;

/**
 * Created by eugeniu on 10/16/17.
 */

public abstract class ListSubscriberRespiration<T> extends SubscriberRespiration<Map<String, T>> {

    @Override
    public void onSuccess(Map<String, T> dataSnapShot) {
        for (Map.Entry<String, T> entry: dataSnapShot.entrySet()) {
            onReceive(entry.getKey(), entry.getValue());
        }
    }

    public abstract void onReceive(String key, T value);
}
