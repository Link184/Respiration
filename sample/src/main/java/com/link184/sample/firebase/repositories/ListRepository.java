package com.link184.sample.firebase.repositories;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.link184.respiration.repository.Configuration;
import com.link184.respiration.repository.FirebaseAuthenticationRequired;
import com.link184.respiration.repository.FirebaseRepository;
import com.link184.respiration.subscribers.SubscriberFirebase;
import com.link184.sample.firebase.SampleFriendModel;

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

/**
 * Created by erza on 9/23/17.
 */

public class ListRepository extends FirebaseRepository<Map<String, SampleFriendModel>> {
    protected ListRepository(Configuration<Map<String, SampleFriendModel>> configuration) {
        super(configuration);
    }

    @Override
    protected void initRepository() {
        if (!accessPrivate || isUserAuthenticated()) {
            dataSnapshot = new HashMap<>();
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        ListRepository.this.dataSnapshot.put(ds.getKey(), ds.getValue(SampleFriendModel.class));
                    }
                    publishSubject.onNext(Notification.createOnNext(ListRepository.this.dataSnapshot));
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

    /**
     * Subscription to specific item.
     * @param itemId firebase object key to subscribe on.
     */
    public void subscribeToItem(String itemId, SubscriberFirebase<SampleFriendModel> subscriber) {
        publishSubject
                .flatMap(new Function<Notification<Map<String, SampleFriendModel>>, ObservableSource<Notification<SampleFriendModel>>>() {
                    @Override
                    public ObservableSource<Notification<SampleFriendModel>> apply(@NonNull Notification<Map<String, SampleFriendModel>> mapNotification) throws Exception {
                        return Observable.create(e -> e.onNext(Notification.createOnNext(mapNotification.getValue().get(itemId))));
                    }
                })
                .subscribe(subscriber);
        if (dataSnapshot != null && dataSnapshot.containsKey(itemId)) {
            subscriber.onNext(Notification.createOnNext(dataSnapshot.get(itemId)));
        }
    }

    public void subscribeToList(SubscriberFirebase<List<SampleFriendModel>> subscriberFirebase) {
        publishSubject.map(this::mapToList)
                .subscribe(subscriberFirebase);
    }

    private Notification<List<SampleFriendModel>> mapToList(Notification<Map<String, SampleFriendModel>> sourceMap) {
        List<SampleFriendModel> resultList = new ArrayList<>();
        for (Map.Entry<String, SampleFriendModel> entry : sourceMap.getValue().entrySet()) {
            resultList.add(entry.getValue());
        }
        return Notification.createOnNext(resultList);
    }

    @Override
    protected final void setValue(Map<String, SampleFriendModel> newValue) {
        //ignored
    }

    @Override
    protected final void removeValue() {
        //ignored
    }

    /**
     * Get value directly from cache without subscription.
     * @param itemId firebase object key.
     */
    public SampleFriendModel getValue(String itemId) {
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

    public void setValue(String itemId, SampleFriendModel newValue) {
        databaseReference.child(itemId).setValue(newValue);
    }

    /**
     * Get items directly form cache without subscription. Use carefully, response may be null.
     */
    public List<SampleFriendModel> getItems() {
        return dataSnapshot != null ? new ArrayList<>(dataSnapshot.values()) : new ArrayList<>();
    }

    public void removeValue(String itemId) {
        dataSnapshot.remove(itemId);
        databaseReference.child(itemId).removeValue();
    }

    public static class Builder {
        private Configuration<Map<String, SampleFriendModel>> configuration;

        /**
         * Simplified form, easier to use for public repositories.
         */
        public Builder() {
            configuration = new Configuration<>(null);
        }

        /**
         * Firebase data persistence.
         */
        public ListRepository.Builder setPersistence(boolean persistence) {
            configuration.setPersistence(persistence);
            return this;
        }

        /**
         * @param databaseChildren enumerate all children to build a {@link DatabaseReference} object.
         */
        public ListRepository.Builder setChildren(String... databaseChildren) {
            configuration.setDatabaseChildren(databaseChildren);
            return this;
        }

        /**
         * Set true if the data is private for non logged in users. That logic will handle all
         * authentication cases. Be careful when repository is already built with no
         * authenticated user with uid in database reference path, just call resetRepository() method
         * after successful authentication with right uid in path.
         */
        public ListRepository.Builder setAccessPrivate(boolean accessPrivate) {
            configuration.setAccessPrivate(accessPrivate);
            return this;
        }

        public ListRepository build() {
            return new ListRepository(configuration);
        }
    }
}
