package com.link184.respiration.repository.base;

import android.support.annotation.Nullable;

import com.link184.respiration.subscribers.SubscriberRespiration;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Ryzen on 3/2/2018.
 */

public abstract class Repository<T> {
    private BehaviorSubject<Notification<T>> behaviorSubject = BehaviorSubject.create();
    protected Class<T> dataSnapshotClass;

    protected abstract void initRepository();

    public void subscribe(SubscriberRespiration<T> subscriber) {
        behaviorSubject.subscribe(subscriber);
    }

    public void subscribe(Consumer<? super T> onNext) {
        behaviorSubject.subscribe(tNotification -> onNext.accept(tNotification.getValue()));
    }

    public void subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        behaviorSubject.subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError);
    }

    public void subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        behaviorSubject.subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete);
    }

    public void subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {
        behaviorSubject.subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete, onSubscribe);
    }

    /**
     * Unleash all reactive power.
     */
    public Observable<Notification<T>> asObservable() {
        return behaviorSubject;
    }


    /**
     * Method is called when new data is received form firebase.
     * @param value new fresh data.
     */
    protected void onNewDataReceived(T value) {
        behaviorSubject.onNext(Notification.createOnNext(value));
    }

    /**
     * Method is called when something went wrong. For example user is not authenticated and
     * access private is set as true or when internet connection is missing.
     */
    protected void onErrorReceived(Throwable error) {
        behaviorSubject.onNext(Notification.createOnError(error));
    }

    protected abstract void setValue(T newValue);

    protected abstract void removeValue();

    /**
     * Return last cached value.
     */
    @Nullable
    public T getValue() {
        if (behaviorSubject.getValue() != null) {
            return behaviorSubject.getValue().getValue();
        }
        return null;
    }
}
