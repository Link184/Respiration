package com.link184.respiration.repository.local;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.link184.respiration.subscribers.SubscriberFirebase;

import io.reactivex.Notification;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class GeneralLocalRepository<M> extends LocalRepository<M> {
    private JsonElement localElementRef;
    private Gson gson;

    public GeneralLocalRepository(Context context, LocalConfiguration localConfiguration) {
        super(context, localConfiguration);
    }

    @Override
    protected void initRepository() {
        localElementRef = rawJsonElement;
        for (String children: databaseChildren) {
            localElementRef = localElementRef.getAsJsonObject().get(children);
        }

        gson = new Gson();
        behaviorSubject.onNext(Notification.createOnNext(gson.fromJson(localElementRef, dataSnapshotClass)));
    }

    @Override
    protected void setValue(M newValue) {
        localElementRef = gson.toJsonTree(newValue, new TypeToken<M>(){}.getType());
        behaviorSubject.onNext(Notification.createOnNext(gson.fromJson(localElementRef, dataSnapshotClass)));
    }

    @Override
    protected void removeValue() {

    }

    private void emmitEvent() {
        behaviorSubject.onNext(behaviorSubject.getValue());
    }

    @Override
    public void subscribe(SubscriberFirebase<M> subscriber) {
        super.subscribe(subscriber);
        subscriber.onNext(behaviorSubject.getValue());
    }

    @Override
    public void subscribe(Consumer<? super M> onNext) {
        super.subscribe(onNext);
        emmitEvent();
    }

    @Override
    public void subscribe(Consumer<? super M> onNext, Consumer<? super Throwable> onError) {
        super.subscribe(onNext, onError);
        emmitEvent();
    }

    @Override
    public void subscribe(Consumer<? super M> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        super.subscribe(onNext, onError, onComplete);
        emmitEvent();
    }

    @Override
    public void subscribe(Consumer<? super M> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {
        super.subscribe(onNext, onError, onComplete, onSubscribe);
        emmitEvent();
    }
}
