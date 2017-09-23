package com.link184.respiration.subscribers;

import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;

public abstract class SingleSubscriberFirebase<T> extends SubscriberFirebase<T> {
    @Override
    public void onNext(@NonNull Notification<T> tNotification) {
        super.onNext(tNotification);
        dispose();
    }
}
