package com.link184.respiration.repository.local;

import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.link184.respiration.repository.base.ListRepository;
import com.link184.respiration.subscribers.ListSubscriberRespiration;
import com.link184.respiration.subscribers.SubscriberRespiration;
import com.link184.respiration.utils.IdGenerator;
import com.link184.respiration.utils.Preconditions;
import com.link184.respiration.utils.RespirationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

import static junit.framework.Assert.assertTrue;

/**
 * Created by eugeniu on 3/6/18.
 */

public class LocalListRepository<M> extends LocalRepository<M> implements ListRepository<M>{
    protected BehaviorSubject<Notification<Map<String, M>>> behaviorSubject;
    private JsonElement localElementRef;

    public LocalListRepository(LocalConfiguration localConfiguration) {
        super(localConfiguration);
    }

    @Override
    protected void initRepository() {
        behaviorSubject = BehaviorSubject.create();
        localElementRef = rawJsonElement;
        for (String children : databaseChildren) {
            localElementRef = localElementRef.getAsJsonObject().get(children);
        }
        if (localElementRef != null && !localElementRef.isJsonNull()) {
            JsonObject asJsonObject = localElementRef.getAsJsonObject();
            Map<String, M> resultMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, JsonElement> entry : asJsonObject.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    resultMap.put(entry.getKey(), gson.fromJson(entry.getValue(), dataSnapshotClass));
                } else {
                    onErrorReceived(new NotListableRepository());
                    return;
                }
            }
            onNewDataReceived(resultMap);
        } else {
            onErrorReceived(new NullLocalDataSnapshot());
        }
    }

    /**
     * Method is called when new data is received form firebase.
     *
     * @param value new fresh data.
     */
    @Override
    public void onNewDataReceived(Map<String, M> value) {
        behaviorSubject.onNext(Notification.createOnNext(value));
    }

    /**
     * Method is called when something went wrong. For example user is not authenticated and
     * access private is set as true or when internet connection is missing.
     */
    public void onErrorReceived(Throwable error) {
        behaviorSubject.onNext(Notification.createOnError(error));
    }

    @Override
    public final void onNewDataReceived(M value) {
    }

    @Override
    public void subscribe(ListSubscriberRespiration<M> subscriber) {
        behaviorSubject.subscribe(subscriber);
    }

    @Override
    public void subscribeToItem(String itemId, SubscriberRespiration<M> subscriber) {
        behaviorSubject
                .flatMap(new Function<Notification<Map<String, M>>, ObservableSource<Notification<M>>>() {
                    @Override
                    public ObservableSource<Notification<M>> apply(@NonNull Notification<Map<String, M>> mapNotification) {
                        return Observable.create(e -> e.onNext(Notification.createOnNext(mapNotification.getValue().get(itemId))));
                    }
                })
                .subscribe(subscriber);
    }

    @Override
    public void subscribeToList(SubscriberRespiration<List<M>> subscriber) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(subscriber);
    }

    @Override
    public void subscribeToList(Consumer<? super List<M>> onNext) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()));
    }

    @Override
    public void subscribeToList(Consumer<? super List<M>> onNext, Consumer<? super Throwable> onError) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError);
    }

    @Override
    public void subscribeToList(Consumer<? super List<M>> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete);
    }

    @Override
    public void subscribeToList(Consumer<? super List<M>> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {
        behaviorSubject.map(RespirationUtils::mapToList)
                .subscribe(tNotification -> onNext.accept(tNotification.getValue()), onError, onComplete, onSubscribe);
    }


    @Override
    public final void setValue(M newValue) {

    }

    @Override
    public void removeValue() {
        writeToFile(JsonNull.INSTANCE, Map.class, null);
    }

    /**
     * Add new value to the list with generated auto id.
     */
    @Override
    public void addValue(M newValue) {
        addValue(newValue, null);
    }

    @Override
    public void addValue(M newValue, DatabaseReference.CompletionListener onCompleteListener) {
        localElementRef.getAsJsonObject().add(IdGenerator.generate(), gson.toJsonTree(newValue));
        writeToFile(localElementRef, dataSnapshotClass, onCompleteListener);
    }

    /**
     * Get value directly from cache without subscription.
     *
     * @param itemId local object key.
     */
    @Override
    @Nullable
    public M getValue(String itemId) {
        return behaviorSubject.getValue().getValue().get(itemId);
    }

    /**
     * Get key of last element directly form cache.
     */
    @Override
    public String getLastKey() {
        Map<String, M> lastItem = behaviorSubject.getValue().getValue();
        if (lastItem == null || lastItem.isEmpty()) {
            return "";
        }
        return new TreeMap<>(lastItem).lastEntry().getKey();
    }

    @Override
    public void setValue(String itemId, M newValue, DatabaseReference.CompletionListener completionListener) {
        JsonElement newJsonElement = gson.toJsonTree(newValue);
        localElementRef.getAsJsonObject().add(itemId, newJsonElement);
        writeToFile(newJsonElement, Map.class, completionListener);
    }

    @Override
    public void setValue(String itemId, M newValue) {
        setValue(itemId, newValue, null);
    }

    /**
     * Replace the whole list in json node.
     */
    @Override
    public void replaceValue(Map<String, M> newValue, DatabaseReference.CompletionListener completionListener) {
        localElementRef = gson.toJsonTree(newValue, newValue.getClass());
        writeToFile(localElementRef, Map.class, completionListener);
    }

    /**
     * Replace the whole list in json node.
     */
    @Override
    public void replaceValue(Map<String, M> newValue) {
        replaceValue(newValue, null);
    }

    /**
     * Get items directly form cache without subscription. Use carefully, response may be empty.
     */
    @Override
    public List<M> getItems() {
        if (behaviorSubject.getValue() != null) {
            Map<String, M> lastItem = behaviorSubject.getValue().getValue();
            return lastItem != null ? new ArrayList<>(lastItem.values()) : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    @Nullable
    public Map<String, M> getItemsAsMap() {
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
        localElementRef.getAsJsonObject().remove(itemId);
        writeToFile(localElementRef, Map.class, completionListener);
    }

    /**
     * Use this method instead of {@link super.asObservable()}
     */
    @Override
    public Observable<Notification<Map<String, M>>> asListObservable() {
        return behaviorSubject;
    }

    /**
     * Reset local repository by a new configuration object.
     *
     * @param localConfiguration new configuration
     */
    public void resetRepository(LocalConfiguration<M> localConfiguration) {
        resetRepository(localConfiguration, false);
    }

    /**
     * Reset local repository by a new configuration object.
     *
     * @param localConfiguration  new configuration
     * @param removeCurrentDbFile pass true to remove current db file form android files dir.
     */
    public void resetRepository(LocalConfiguration<M> localConfiguration, boolean removeCurrentDbFile) {
        if (removeCurrentDbFile) {
            File dbFile = new File(Preconditions.checkNotNull(localConfiguration.getContext())
                    .getFilesDir(), localConfiguration.getDbName());
            if (dbFile.exists()) {
                boolean deleted = dbFile.delete();
                assertTrue("Failed to remove test db file", deleted);
            }
        }
        initDBFile(localConfiguration);
        initRepository();
    }
}
