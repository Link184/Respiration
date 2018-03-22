package com.link184.respiration.repository.local;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import io.reactivex.Notification;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class GeneralLocalRepository<M> extends LocalRepository<M> {
    private JsonElement localElementRef;

    public GeneralLocalRepository(LocalConfiguration localConfiguration) {
        super(localConfiguration);
    }

    @Override
    protected void initRepository() {
        localElementRef = rawJsonElement;
        for (String children: databaseChildren) {
            localElementRef = localElementRef.getAsJsonObject().get(children);
        }
        if (localElementRef != null && !localElementRef.isJsonNull()) {
            behaviorSubject.onNext(Notification.createOnNext(gson.fromJson(localElementRef, dataSnapshotClass)));
        } else {
            behaviorSubject.onNext(Notification.createOnError(new NullLocalDataSnapshot()));
        }
    }

    @Override
    public void setValue(M newValue) {
        localElementRef = gson.toJsonTree(newValue, dataSnapshotClass);
        writeToFile(localElementRef);
    }

    @Override
    public void removeValue() {
        writeToFile(JsonNull.INSTANCE);
    }
}
