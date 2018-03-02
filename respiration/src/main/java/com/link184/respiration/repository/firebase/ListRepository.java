package com.link184.respiration.repository.firebase;

import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by erza on 9/23/17.
 */

public class ListRepository<T> extends FirebaseRepository<T> {
    protected BehaviorSubject<Notification<Map<String, T>>> behaviorSubject;

    protected ListRepository(Configuration<T> configuration) {
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

    protected void onNewDataReceived(Map<String, T> value) {
        behaviorSubject.onNext(Notification.createOnNext(value));
    }

    @Override
    protected void onErrorReceived(Throwable error) {
        behaviorSubject.onNext(Notification.createOnError(error));
    }

    private void removeListener() {
        if (databaseReference != null && valueListener != null) {
            databaseReference.removeEventListener(valueListener);
        }
    }

    public void subscribe(ListSubscriberFirebase<T> subscriber) {
        behaviorSubject.subscribe(subscriber);
    }

    /**
     * Subscription to specific item.
     *
     * @param itemId firebase object key to subscribe on.
     */
    public void subscribeToItem(String itemId, SubscriberFirebase<T> subscriber) {
        behaviorSubject
                .flatMap(new Function<Notification<Map<String, T>>, ObservableSource<Notification<T>>>() {
                    @Override
                    public ObservableSource<Notification<T>> apply(@NonNull Notification<Map<String, T>> mapNotification) throws Exception {
                        return Observable.create(e -> e.onNext(Notification.createOnNext(mapNotification.getValue().get(itemId))));
                    }
                })
                .subscribe(subscriber);
    }

    public void subscribeToList(SubscriberFirebase<List<T>> subscriber) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(subscriber);
    }

    public void subscribeToList(Consumer<? super List<T>> onNext) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()));
    }

    public void subscribeToList(Consumer<? super List<T>> onNext, Consumer<? super Throwable> onError) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError);
    }

    public void subscribeToList(Consumer<? super List<T>> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete);
    }

    public void subscribeToList(Consumer<? super List<T>> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete, onSubscribe);
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
    @Nullable
    public T getValue(String itemId) {
        return behaviorSubject.getValue().getValue().get(itemId);
    }

    /**
     * Get key of last element directly form cache.
     */
    public String getLastKey() {
        Map<String, T> lastItem = behaviorSubject.getValue().getValue();
        if (lastItem == null || lastItem.isEmpty()) {
            return "";
        }
        return new TreeMap<>(lastItem).lastEntry().getKey();
    }

    public void setValue(String itemId, T newValue) {
        databaseReference.child(itemId).setValue(newValue);
    }

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

    public void removeValue(String itemId) {
        databaseReference.child(itemId).removeValue();
    }

    public void removeValue(String itemId, @NonNull OnCompleteListener<Void> onCompeteListener) {
        databaseReference.child(itemId).removeValue().addOnCompleteListener(onCompeteListener);
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
