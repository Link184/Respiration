package com.link184.respiration.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.link184.respiration.subscribers.ListSubscriberFirebase;
import com.link184.respiration.subscribers.SubscriberFirebase;
import com.link184.respiration.utils.RespirationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by erza on 9/23/17.
 */

public class ListRepository<T> extends FirebaseRepository<T> {
    protected Map<String, T> dataSnapshot;
    protected PublishSubject<Notification<Map<String, T>>> publishSubject;

    protected ListRepository(Configuration<T> configuration) {
        super(configuration);
        this.publishSubject = PublishSubject.create();
    }

    @Override
    protected void initRepository() {
        if (!accessPrivate || isUserAuthenticated()) {
            this.dataSnapshot = null;
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ListRepository.this.dataSnapshot = new HashMap<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ListRepository.this.dataSnapshot.put(ds.getKey(), ds.getValue(dataSnapshotClass));
                        }
                        publishSubject.onNext(Notification.createOnNext(ListRepository.this.dataSnapshot));
                    } else {
                        ListRepository.this.dataSnapshot = null;
                        publishSubject.onNext(Notification.createOnError(new NullFirebaseDataSnapshot()));
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

    public void subscribe(ListSubscriberFirebase<T> subscriber) {
        publishSubject.subscribe(subscriber);
        if (dataSnapshot != null) {
            subscriber.onNext(Notification.createOnNext(dataSnapshot));
        } else {
            subscriber.onNext(Notification.createOnError(new NullFirebaseDataSnapshot()));
        }
    }

    /**
     * Subscription to specific item.
     *
     * @param itemId firebase object key to subscribe on.
     */
    public void subscribeToItem(String itemId, SubscriberFirebase<T> subscriber) {
        publishSubject
                .flatMap(new Function<Notification<Map<String, T>>, ObservableSource<Notification<T>>>() {
                    @Override
                    public ObservableSource<Notification<T>> apply(@NonNull Notification<Map<String, T>> mapNotification) throws Exception {
                        return Observable.create(e -> e.onNext(Notification.createOnNext(mapNotification.getValue().get(itemId))));
                    }
                })
                .subscribe(subscriber);
        if (dataSnapshot != null && dataSnapshot.containsKey(itemId)) {
            subscriber.onNext(Notification.createOnNext(dataSnapshot.get(itemId)));
        }
    }

    public void subscribeToList(SubscriberFirebase<List<T>> subscriber) {
        publishSubject.map(RespirationUtils::mapToList)
                .subscribe(subscriber);
        if (dataSnapshot != null) {
            subscriber.onNext(RespirationUtils.mapToList(Notification.createOnNext(dataSnapshot)));
        } else {
            subscriber.onNext(Notification.createOnError(new NullFirebaseDataSnapshot()));
        }
    }



    /**
     * Add new value to the list with firebase auto id.
     */
    public void addValue(T newValue) {
        databaseReference.push().setValue(newValue);
    }

    public final void addValue(T newValue, com.google.firebase.database.DatabaseReference.CompletionListener onCompleteListener) {
        databaseReference.push().setValue(newValue, onCompleteListener);
    }

    @Override
    protected final void setValue(T newValue) {

    }

    @Override
    protected final void removeValue() {
        //ignored
    }

    /**
     * Get value directly from cache without subscription.
     *
     * @param itemId firebase object key.
     */
    public T getValue(String itemId) {
        return dataSnapshot.get(itemId);
    }

    /**
     * Get key of last element directly form cache.
     */
    public String getLastKey() {
        if (dataSnapshot.isEmpty()) {
            return "";
        }
        return new TreeMap<>(dataSnapshot).lastEntry().getKey();
    }

    public void setValue(String itemId, T newValue) {
        databaseReference.child(itemId).setValue(newValue);
    }

    /**
     * Get items directly form cache without subscription. Use carefully, response may be null.
     */
    public List<T> getItems() {
        return dataSnapshot != null ? new ArrayList<>(dataSnapshot.values()) : new ArrayList<>();
    }

    public void removeValue(String itemId) {
        dataSnapshot.remove(itemId);
        databaseReference.child(itemId).removeValue();
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

    public Observable<Notification<Map<String, T>>> asListObservable() {
        return publishSubject;
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
        public ListRepository.Builder<M> setPersistence(boolean persistence) {
            configuration.setPersistence(persistence);
            return this;
        }

        /**
         * @param databaseChildren enumerate all children to build a {@link DatabaseReference} object.
         */
        public ListRepository.Builder<M> setChildren(String... databaseChildren) {
            configuration.setDatabaseChildren(databaseChildren);
            return this;
        }

        /**
         * Set true if the data is private for non logged in users. That logic will handle all
         * authentication cases. Be careful when repository is already built with no
         * authenticated user with uid in database reference path, just call resetRepository() method
         * after successful authentication with right uid in path.
         */
        public ListRepository.Builder<M> setAccessPrivate(boolean accessPrivate) {
            configuration.setAccessPrivate(accessPrivate);
            return this;
        }

        public ListRepository<M> build() {
            return new ListRepository<>(configuration);
        }
    }
}
