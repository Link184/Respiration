package com.link184.respiration.repository.local;

import android.content.Context;

import com.google.gson.JsonElement;

import io.reactivex.Notification;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class GeneralLocalRepository<M> extends LocalRepository<M> {
    private JsonElement localElementRef;

    public GeneralLocalRepository(Context context, LocalConfiguration localConfiguration) {
        super(context, localConfiguration);
    }

    @Override
    protected void initRepository() {
        localElementRef = rawJsonElement;
        for (String children: databaseChildren) {
            localElementRef = localElementRef.getAsJsonObject().get(children);
        }

        behaviorSubject.onNext(Notification.createOnNext(gson.fromJson(localElementRef, dataSnapshotClass)));
    }

    @Override
    public void setValue(M newValue) {
        localElementRef = gson.toJsonTree(newValue, dataSnapshotClass);
        writeToFile(localElementRef);
    }

    @Override
    protected void removeValue() {

    }
}
