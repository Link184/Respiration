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
            if (configuration.isChildrenSensitive()) {
                resetRepository(configuration.getDatabaseChildren(firebaseAuth1.getCurrentUser()));
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

    /**
     * Unleash all reactive power.
     */
    public Observable<Notification<T>> asObservable() {
        return behaviorSubject;
    }

    /**
     * @return true if user is authenticated to firebase.
     */
    public boolean isUserAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * @return firebase user id.
     */
    public String getUserId() {
        if (isUserAuthenticated()) {
            return firebaseAuth.getCurrentUser().getUid();
        }
        return null;
    }

    protected abstract void initRepository();

    /**
     * Reset firebase database reference children.
     * @param databaseChildren new children to replace the old ones.
     */
    public abstract void resetRepository(String... databaseChildren);

    /**
     * Method is called when new data is received form firebase.
     * @param value new fresh data.
     */
    protected void onNewDataReceived(T value) {

    }

    /**
     * Method is called when something went wrong. For example user is not authenticated and
     * access private is set as true or when internet connection is missing.
     */
    protected void onErrorReceived(Throwable error) {

    }

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

    /**
     * @return {@link FirebaseAuth}
     */
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    protected abstract void setValue(T newValue);

    protected abstract void removeValue();
}
