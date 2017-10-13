package com.link184.respiration.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.link184.respiration.subscribers.SubscriberFirebase;

import io.reactivex.Notification;

public class GeneralRepository<M> extends FirebaseRepository<M> {
    protected GeneralRepository(Configuration<M> repositoryConfig) {
        super(repositoryConfig);
    }

    @Override
    protected final void initRepository() {
        if (!accessPrivate || isUserAuthenticated()) {
            dataSnapshot = null;
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GeneralRepository.this.dataSnapshot = dataSnapshot.getValue(dataSnapshotClass);
                    if (GeneralRepository.this.dataSnapshot != null) {
                        publishSubject.onNext(Notification.createOnNext(GeneralRepository.this.dataSnapshot));
                    } else {
                        publishSubject.onNext(Notification.createOnError(new NullFirebaseDataSnapshot("Null data from firebase.")));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    publishSubject.onNext(Notification.createOnError(databaseError.toException()));
                }
            };
            databaseReference.addValueEventListener(valueListener);
        } else {
            removeListener();
            dataSnapshot = null;
            publishSubject.onNext(Notification.createOnError(new FirebaseAuthenticationRequired()));
        }
    }

    private void removeListener() {
        if (databaseReference != null && valueListener != null) {
            databaseReference.removeEventListener(valueListener);
        }
    }

    @Override
    public void subscribe(SubscriberFirebase<M> subscriber) {
        publishSubject.subscribe(subscriber);
        if (dataSnapshot != null) {
            subscriber.onNext(Notification.createOnNext(dataSnapshot));
        } else {
            subscriber.onNext(Notification.createOnError(new NullFirebaseDataSnapshot("Data is null.")));
        }
    }

    public void resetRepository(String... databaseChildren) {
        removeListener();
        StringBuilder sb = new StringBuilder();
        for (String child : databaseChildren) {
            sb.append(child).append("/");
        }
        databaseReference = database.getReference(sb.toString());
        initRepository();
    }

    public void setValue(M newValue) {
        databaseReference.setValue(newValue);
    }

    public void setValue(M newValue, OnCompleteListener onCompleteListener) {
        databaseReference.setValue(newValue, onCompleteListener);
    }

    public void removeValue() {
        dataSnapshot = null;
        databaseReference.removeValue();
    }

    public static class Builder<M> {
        private Configuration<M> configuration;

        /**
         * @param dataSnapshotType just a firebase model Class. Because of erasing is impossible take
         *                         java class type form generic in runtime. So we are forced to ask
         *                         model type explicitly in constructor alongside generic type.
         */
        public Builder(Class<M> dataSnapshotType) {
            configuration = new Configuration<>(dataSnapshotType);
        }

        /**
         * Firebase data persistence.
         */
        public Builder<M> setPersistence(boolean persistence) {
            configuration.setPersistence(persistence);
            return this;
        }

        /**
         * @param databaseChildren enumerate all children to build a {@link DatabaseReference} object.
         */
        public Builder<M> setChildren(String... databaseChildren) {
            configuration.setDatabaseChildren(databaseChildren);
            return this;
        }

        /**
         * Set true if the data is private for non logged in users. That logic will handle all
         * authentication cases. Be careful when repository is already built with no
         * authenticated user with uid in database reference path, just call resetRepository() method
         * after successful authentication with right uid in path.
         */
        public Builder<M> setAccessPrivate(boolean accessPrivate) {
            configuration.setAccessPrivate(accessPrivate);
            return this;
        }

        public GeneralRepository<M> build() {
            return new GeneralRepository<>(configuration);
        }
    }
}
