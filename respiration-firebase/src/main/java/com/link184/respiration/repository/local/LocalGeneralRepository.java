package com.link184.respiration.repository.local;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.link184.respiration.utils.Preconditions;

import java.io.File;

import io.reactivex.Notification;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class LocalGeneralRepository<M> extends LocalRepository<M> {
    private JsonElement localElementRef;

    public LocalGeneralRepository(LocalConfiguration localConfiguration) {
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

    /**
     * Reset local repository by a new configuration object.
     * @param localConfiguration new configuration
     */
    public void resetRepository(LocalConfiguration<M> localConfiguration) {
        resetRepository(localConfiguration, false);
    }

    /**
     * Reset local repository by a new configuration object.
     * @param localConfiguration new configuration
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
