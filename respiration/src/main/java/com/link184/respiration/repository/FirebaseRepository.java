package com.link184.respiration.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.link184.respiration.BuildConfig;
import com.link184.respiration.subscribers.SubscriberFirebase;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

abstract class FirebaseRepository<T> {
    protected final String TAG = getClass().getSimpleName();

    protected static FirebaseDatabase database;
    protected static FirebaseAuth firebaseAuth;
    protected DatabaseReference databaseReference;
    protected T dataSnapshot;
    protected Class<T> dataSnapshotClass;
    protected PublishSubject<Notification<T>> publishSubject;
    protected boolean accessPrivate;

    FirebaseRepository(boolean persistence, boolean accessPrivate, Configuration<T> repositoryConfig) {
        this.accessPrivate = accessPrivate;
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(persistence);
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
            initAuthStateListener();
        } else {
            initRepository();
        }
        publishSubject = PublishSubject.create();
    }

    private void initAuthStateListener() {
        firebaseAuth.addAuthStateListener(firebaseAuth1 -> initRepository());
    }

    public void subscribe(SubscriberFirebase<T> subscriber) {
        publishSubject.subscribe(subscriber);
        if (dataSnapshot != null) {
            subscriber.onNext(Notification.createOnNext(dataSnapshot));
        }
    }

    public Observable<Notification<T>> asObservable() {
        return publishSubject;
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

    /**
     * Return last cached value.
     */
    public Single<Notification<T>> getValue() {
        return dataSnapshot != null
                ? publishSubject.last(Notification.createOnNext(dataSnapshot))
                : publishSubject.lastOrError();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    protected abstract void setValue(T newValue);

    protected abstract void removeValue();
}
