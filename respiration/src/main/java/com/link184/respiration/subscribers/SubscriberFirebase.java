package com.link184.respiration.subscribers;

import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public abstract class SubscriberFirebase<T> extends DisposableObserver<Notification<T>> {

    public abstract void onSuccess(T dataSnapShot);

    public void onFailure(Throwable error) {
        dispose();
    }

    @Override
    public final void onNext(@NonNull Notification<T> tNotification) {
        if (tNotification.isOnNext()) {
            onSuccess(tNotification.getValue());
        }
        if (tNotification.isOnError()) {
            onFailure(tNotification.getError());
        }
    }

    @Override
    public final void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public final void onComplete() {
    }
}
