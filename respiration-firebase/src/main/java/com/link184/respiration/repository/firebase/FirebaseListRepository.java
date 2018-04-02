package com.link184.respiration.repository.firebase;

import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.link184.respiration.repository.base.ListRepository;
import com.link184.respiration.subscribers.ListSubscriberRespiration;
import com.link184.respiration.subscribers.SubscriberRespiration;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by erza on 9/23/17.
 */

public class FirebaseListRepository<T> extends FirebaseRepository<T> implements ListRepository<T>{
    protected BehaviorSubject<Notification<Map<String, T>>> behaviorSubject;

    protected FirebaseListRepository(Configuration<T> configuration) {
        super(configuration);
        this.behaviorSubject = BehaviorSubject.create();
    }

    @Override
    protected void initRepository() {
        if (!accessPrivate || isUserAuthenticated()) {
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, T> itemMap = new HashMap<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            itemMap.put(ds.getKey(), ds.getValue(dataSnapshotClass));
                        }
                        onNewDataReceived(itemMap);
                    } else {
                        onErrorReceived(new NullFirebaseDataSnapshot());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    onErrorReceived(databaseError.toException());
                }
            };
            databaseReference.addValueEventListener(valueListener);
        } else {
            removeListener();
            onErrorReceived(new FirebaseAuthenticationRequired());
        }
    }

    public void onNewDataReceived(Map<String, T> value) {
        behaviorSubject.onNext(Notification.createOnNext(value));
    }

    @Override
    public void onErrorReceived(Throwable error) {
        behaviorSubject.onNext(Notification.createOnError(error));
    }

    private void removeListener() {
        if (databaseReference != null && valueListener != null) {
            databaseReference.removeEventListener(valueListener);
        }
    }

    @Override
    public void subscribe(ListSubscriberRespiration<T> subscriber) {
        behaviorSubject.subscribe(subscriber);
    }

    /**
     * Subscription to specific item.
     *
     * @param itemId firebase object key to subscribe on.
     */
    @Override
    public void subscribeToItem(String itemId, SubscriberRespiration<T> subscriber) {
        behaviorSubject
                .flatMap(new Function<Notification<Map<String, T>>, ObservableSource<Notification<T>>>() {
                    @Override
                    public ObservableSource<Notification<T>> apply(@NonNull Notification<Map<String, T>> mapNotification) {
                        return Observable.create(e -> e.onNext(Notification.createOnNext(mapNotification.getValue().get(itemId))));
                    }
                })
                .subscribe(subscriber);
    }

    @Override
    public void subscribeToList(SubscriberRespiration<List<T>> subscriber) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(subscriber);
    }

    @Override
    public void subscribeToList(Consumer<? super List<T>> onNext) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()));
    }

    @Override
    public void subscribeToList(Consumer<? super List<T>> onNext, Consumer<? super Throwable> onError) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError);
    }

    @Override
    public void subscribeToList(Consumer<? super List<T>> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete);
    }

    @Override
    public void subscribeToList(Consumer<? super List<T>> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete, onSubscribe);
    }

    /**
     * Add new value to the list with firebase auto id.
     */
    @Override
    public void addValue(T newValue) {
        addValue(newValue, null);
    }

    @Override
    public final void addValue(T newValue, com.google.firebase.database.DatabaseReference.CompletionListener onCompleteListener) {
        databaseReference.push().setValue(newValue, onCompleteListener);
    }

    @Override
    public final void setValue(T newValue) {

    }

    /**
     * Remove all node.
     */
    @Override
    public void removeValue() {
        databaseReference.removeValue();
    }

    /**
     * Get value directly from cache without subscription.
     *
     * @param itemId firebase object key.
     */
    @Override
    @Nullable
    public T getValue(String itemId) {
        return behaviorSubject.getValue().getValue().get(itemId);
    }

    /**
     * Get key of last element directly form cache.
     */
    @Override
    public String getLastKey() {
        Map<String, T> lastItem = behaviorSubject.getValue().getValue();
        if (lastItem == null || lastItem.isEmpty()) {
            return "";
        }
        return new TreeMap<>(lastItem).lastEntry().getKey();
    }

    @Override
    public void setValue(String itemId, T newValue) {
        databaseReference.child(itemId).setValue(newValue);
    }

    @Override
    public void replaceValue(Map<String, T> newValue, DatabaseReference.CompletionListener completionListener) {
        databaseReference.setValue(newValue, completionListener);
    }

    @Override
    public void replaceValue(Map<String, T> newValue) {
        replaceValue(newValue, null);
    }

    @Override
    public void setValue(String itemId, T newValue, DatabaseReference.CompletionListener completionListener) {
        databaseReference.child(itemId).setValue(newValue, completionListener);
    }

    /**
     * Get items directly form cache without subscription. Use carefully, response may be empty.
     */
    public List<T> getItems() {
        if (behaviorSubject.getValue() != null) {
            Map<String, T> lastItem = behaviorSubject.getValue().getValue();
            return lastItem != null ? new ArrayList<>(lastItem.values()) : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Nullable
    public Map<String, T> getItemsAsMap() {
        if (behaviorSubject.getValue() != null) {
            return behaviorSubject.getValue().getValue();
        }
        return null;
    }

    @Override
    public void removeValue(String itemId) {
        removeValue(itemId, null);
    }

    @Override
    public void removeValue(String itemId, DatabaseReference.CompletionListener completionListener) {
        databaseReference.child(itemId).removeValue(completionListener);
    }

    @Override
    public void resetRepository(String... databaseChildren) {
        removeListener();
        StringBuilder sb = new StringBuilder();
        for (String child : databaseChildren) {
            sb.append(child).append("/");
        }
        databaseReference = database.getReference(sb.toString());
        initRepository();
    }

    /**
     * Use this method instead of {@link super.asObservable()}
     */
    public Observable<Notification<Map<String, T>>> asListObservable() {
        return behaviorSubject;
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
        public FirebaseListRepository.Builder<M> setPersistence(boolean persistence) {
            configuration.setPersistence(persistence);
            return this;
        }

        /**
         * @param databaseChildren enumerate all children to build a {@link DatabaseReference} object.
         */
        public FirebaseListRepository.Builder<M> setChildren(String... databaseChildren) {
            configuration.setDatabaseChildren(databaseChildren);
            return this;
        }

        /**
         * Set true if the data is private for non logged in users. That logic will handle all
         * authentication cases. Be careful when repository is already built with no
         * authenticated user with uid in database reference path, just call resetRepository() method
         * after successful authentication with right uid in path.
         */
        public FirebaseListRepository.Builder<M> setAccessPrivate(boolean accessPrivate) {
            configuration.setAccessPrivate(accessPrivate);
            return this;
        }

        public FirebaseListRepository<M> build() {
            return new FirebaseListRepository<>(configuration);
        }
    }
}
