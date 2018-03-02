package com.link184.respiration.repository.firebase;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.link184.respiration.BuildConfig;
import com.link184.respiration.repository.base.Repository;

import io.reactivex.subjects.BehaviorSubject;

abstract class FirebaseRepository<T> extends Repository<T>{
    protected final String TAG = getClass().getSimpleName();

    protected static FirebaseDatabase database;
    protected static FirebaseAuth firebaseAuth;
    protected DatabaseReference databaseReference;
    protected ValueEventListener valueListener;
    protected Class<T> dataSnapshotClass;
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

    /**
     * Reset firebase database reference children.
     * @param databaseChildren new children to replace the old ones.
     */
    public abstract void resetRepository(String... databaseChildren);

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
}
