package com.link184.respiration.repository.local;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.link184.respiration.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by eugeniu on 3/6/18.
 */

public class LocalListRepository<M> extends LocalRepository<M>{
    protected BehaviorSubject<Notification<Map<String, M>>> behaviorSubject;
    private JsonElement localElementRef;

    LocalListRepository(Context context, LocalConfiguration localConfiguration) {
        super(context, localConfiguration);
    }

    @Override
    protected void initRepository() {
        localElementRef = rawJsonElement;
        for (String children: databaseChildren) {
            localElementRef = localElementRef.getAsJsonObject().get(children);
        }
        if (localElementRef != null && !localElementRef.isJsonNull()) {
            JsonObject asJsonObject = localElementRef.getAsJsonObject();
            Map<String, M> resultMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, JsonElement> entry: asJsonObject.entrySet()) {
                resultMap.put(entry.getKey(), gson.fromJson(entry.getValue(), dataSnapshotClass));
            }
            behaviorSubject.onNext(Notification.createOnNext(resultMap));
        } else {
            behaviorSubject.onNext(Notification.createOnError(new NullLocalDataSnapshot()));
        }
    }

    @Override
    protected final void setValue(M newValue) {

    }

    @Override
    protected void removeValue() {
        writeToFile(JsonNull.INSTANCE);
    }

    /**
     * Add new value to the list with generated auto id.
     */
    public void addValue(M newValue) {
        localElementRef.getAsJsonObject().add(IdGenerator.generate(), gson.toJsonTree(newValue));
    }

    /**
     * Get value directly from cache without subscription.
     *
     * @param itemId local object key.
     */
    @Nullable
    public M getValue(String itemId) {
        return behaviorSubject.getValue().getValue().get(itemId);
    }

    /**
     * Get key of last element directly form cache.
     */
    public String getLastKey() {
        Map<String, M> lastItem = behaviorSubject.getValue().getValue();
        if (lastItem == null || lastItem.isEmpty()) {
            return "";
        }
        return new TreeMap<>(lastItem).lastEntry().getKey();
    }

    public void setValue(String itemId, M newValue) {
        JsonElement newJsonElement = gson.toJsonTree(newValue);
        localElementRef.getAsJsonObject().add(itemId, newJsonElement);
        writeToFile(newJsonElement);
    }

    /**
     * Get items directly form cache without subscription. Use carefully, response may be empty.
     */
    public List<M> getItems() {
        if (behaviorSubject.getValue() != null) {
            Map<String, M> lastItem = behaviorSubject.getValue().getValue();
            return lastItem != null ? new ArrayList<>(lastItem.values()) : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Nullable
    public Map<String, M> getItemsAsMap() {
        if (behaviorSubject.getValue() != null) {
            return behaviorSubject.getValue().getValue();
        }
        return null;
    }

    public void removeValue(String itemId) {
        localElementRef.getAsJsonObject().remove(itemId);
        writeToFile(localElementRef);
    }

    /**
     * Use this method instead of {@link super.asObservable()}
     */
    public Observable<Notification<Map<String, M>>> asListObservable() {
        return behaviorSubject;
    }
}
