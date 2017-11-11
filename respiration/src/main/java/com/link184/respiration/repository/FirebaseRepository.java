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
            initAuthStateListener();
        } else {
            initRepository();
        }
        behaviorSubject = BehaviorSubject.create();
    }

    private void initAuthStateListener() {
        firebaseAuth.addAuthStateListener(firebaseAuth1 -> initRepository());
    }

    public void subscribe(SubscriberFirebase<T> subscriber) {
        behaviorSubject.subscribe(subscriber);
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
