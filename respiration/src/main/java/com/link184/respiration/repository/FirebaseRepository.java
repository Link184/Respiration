package com.link184.respiration.repository;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.link184.respiration.BuildConfig;
import com.link184.respiration.subscribers.SubscriberFirebase;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

abstract class FirebaseRepository<T> {
    protected final String TAG = getClass().getSimpleName();

    protected static FirebaseDatabase database;
    protected static FirebaseAuth firebaseAuth;
    protected DatabaseReference databaseReference;
    protected ValueEventListener valueListener;
    protected Class<T> dataSnapshotClass;
    protected BehaviorSubject<Notification<T>> behaviorSubject;
    protected boolean accessPrivate;

    FirebaseRepository(Configuration<T> repositoryConfig) {
        this.accessPrivate = repositoryConfig.isAccessPrivate();
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(repositoryConfig.isPersistence());
            if (BuildConfig.DEBUG) {
                database.setLogLevel(Logger.Level.DEBUG);
            }
        }
        databaseReference = database.getReference(repositoryConfig.getDatabaseChildren());
        dataSnapshotClass = repositoryConfig.getDataSnapshotType();
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        if (accessPrivate) {
            initAuthStateListener(repositoryConfig);
        } else {
            initRepository();
        }
        behaviorSubject = BehaviorSubject.create();
    }

    private void initAuthStateListener(Configuration<T> configuration) {
        firebaseAuth.addAuthStateListener(firebaseAuth1 -> {
            initRepository();
            if (configuration.isChildrenSensitive() && firebaseAuth1.getCurrentUser() != null) {
                resetRepository(configuration.getDatabaseChildren(firebaseAuth1.getCurrentUser().getUid()));
            }
        });
    }

    public void subscribe(SubscriberFirebase<T> subscriber) {
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

    public Observable<Notification<T>> asObservable() {
        return behaviorSubject;
    }

    public boolean isUserAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getUserId() {
        if (isUserAuthenticated()) {
            return firebaseAuth.getCurrentUser().getUid();
        }
        return null;
    }

    protected abstract void initRepository();

    public abstract void resetRepository(String... databaseChildren);

    /**
     * Return last cached value.
     */
    @Nullable
    public T getValue() {
        return behaviorSubject.getValue().getValue();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    protected abstract void setValue(T newValue);

    protected abstract void removeValue();
}
