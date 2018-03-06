package com.link184.respiration.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.link184.respiration.repository.base.Repository;
import com.link184.respiration.utils.Preconditions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.reactivex.Notification;

/**
 * Created by eugeniu on 3/2/18.
 */

abstract class LocalRepository<T> extends Repository<T> {
    protected final Gson gson;
    protected static JsonElement rawJsonElement;
    protected final String[] databaseChildren;

    private final File dbAndroidLocation;
    private final Lock writeLock;
    private final WriteHandler writeHandler;

    LocalRepository(Context context, LocalConfiguration localConfiguration) {
        this.dbAndroidLocation = new File(context.getFilesDir(), localConfiguration.getDbName());
        if (rawJsonElement == null) {
            if (dbAndroidLocation.exists()) {
                rawJsonElement = loadJsonFile();
            } else {
                loadFromAssets(context, localConfiguration.getAssetDbFilePath());
                rawJsonElement = loadJsonFile();
            }
        }
        this.gson = new Gson();
        this.databaseChildren = Preconditions.checkNotNull(localConfiguration.getDatabaseChildren());
        this.dataSnapshotClass = localConfiguration.getDataSnapshotType();
        this.writeLock = new ReentrantReadWriteLock().writeLock();
        this.writeHandler = new WriteHandler("RESPIRATION_IO_HANDLER");

        initRepository();
    }

    /**
     * Load database form android files dir.
     */
    @Nullable
    private JsonElement loadJsonFile() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(dbAndroidLocation));
            return gson.toJsonTree(gson.fromJson(reader, Object.class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load json file template from assets and copy it to androids files dir.
     */
    @Nullable
    private JsonElement loadFromAssets(Context context, String filePath) {
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            FileOutputStream outputStream = new FileOutputStream(dbAndroidLocation);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return loadJsonFile();
        } catch (IOException e) {
            behaviorSubject.onNext(Notification.createOnError(e));
        }
        return null;
    }

    /**
     * Write db changes to android files dir.
     * @param newContent new content which should be stored into db file.
     */
    protected void writeToFile(@NonNull JsonElement newContent) {
        writeHandler.post(() -> {
            writeLock.lock();
            JsonElement[] elementsToUpdate = new JsonElement[databaseChildren.length - 1];
            for (int i = 0; i < databaseChildren.length - 1; i++) {
                elementsToUpdate[i] = rawJsonElement.getAsJsonObject().get(databaseChildren[i]);
            }
            elementsToUpdate[elementsToUpdate.length - 1].getAsJsonObject()
                    .add(databaseChildren[databaseChildren.length - 1], newContent);
            for (int i = elementsToUpdate.length - 2; i >= 0; i--) {
                elementsToUpdate[i].getAsJsonObject().add(databaseChildren[i], elementsToUpdate[i]);
            }
            rawJsonElement.getAsJsonObject().add(databaseChildren[0], elementsToUpdate[0]);

            try {
                InputStream inputStream = new ByteArrayInputStream(new Gson().toJson(rawJsonElement)
                        .getBytes("UTF-8"));
                FileOutputStream outputStream = new FileOutputStream(dbAndroidLocation);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                if (!newContent.isJsonNull()) {
                    behaviorSubject.onNext(Notification.createOnNext(gson.fromJson(newContent, dataSnapshotClass)));
                } else {
                    behaviorSubject.onNext(Notification.createOnError(new NullLocalDataSnapshot()));
                }
            } catch (IOException e) {
                behaviorSubject.onNext(Notification.createOnError(e));
            }
            writeLock.unlock();
        });
    }
}
