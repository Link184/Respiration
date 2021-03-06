package com.link184.respiration.repository.local;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by eugeniu on 3/2/18.
 */

abstract class LocalRepository<T> extends Repository<T> {
    protected final Gson gson;
    protected JsonElement rawJsonElement;
    protected String[] databaseChildren;

    private final File dbAndroidLocation;
    private final Lock writeLock;
    private final WriteHandler writeHandler;

    LocalRepository(LocalConfiguration localConfiguration) {
        this.dbAndroidLocation = new File(localConfiguration.getContext().getFilesDir(), localConfiguration.getDbName());
        this.gson = new GsonBuilder()
                .setExclusionStrategies(new LocalFirebaseExclusionStrategy())
                .create();
        this.writeLock = new ReentrantReadWriteLock().writeLock();
        this.writeHandler = new WriteHandler("RESPIRATION_IO_HANDLER");

        initDBFile(localConfiguration);
        initRepository();
    }

    void initDBFile(LocalConfiguration localConfiguration) {
        if (dbAndroidLocation.exists()) {
            rawJsonElement = loadJsonFile();
        } else {
            loadFromAssets(localConfiguration.getContext(), localConfiguration.getAssetDbFilePath());
            rawJsonElement = loadJsonFile();
        }
        this.databaseChildren = Preconditions.checkNotNull(localConfiguration.getDatabaseChildren());
        this.dataSnapshotClass = localConfiguration.getDataSnapshotType();
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
            writeLock.lock();
            InputStream inputStream = context.getAssets().open(filePath);
            FileOutputStream outputStream = new FileOutputStream(dbAndroidLocation);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            writeLock.unlock();
            return loadJsonFile();
        } catch (IOException e) {
            e.printStackTrace();
            writeLock.unlock();
            onErrorReceived(e);
        }
        return null;
    }

    /**
     * Write db changes to android files dir.
     *
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
                    onNewDataReceived(gson.fromJson(newContent, dataSnapshotClass));
                } else {
                    onErrorReceived(new NullLocalDataSnapshot());
                }
            } catch (IOException e) {
                onErrorReceived(e);
            }
            writeLock.unlock();
        });
    }

    /**
     * Write db changes to android files dir. Designed for list repositories.
     *
     * @param newContent new content which should be stored into db file.
     */
    protected <M> void writeToFile(@NonNull JsonElement newContent,
                                              @NonNull Class<M> dataSnapshotClass,
                                              @Nullable DatabaseReference.CompletionListener completionListener) {
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
                if (completionListener != null) {
                    if (!newContent.isJsonNull()) {
                        completionListener.onComplete(null, gson.fromJson(newContent, dataSnapshotClass));
                    } else {
                        completionListener.onComplete(new NullLocalDataSnapshot(), null);
                    }
                }
            } catch (IOException e) {
                if (completionListener != null) {
                    completionListener.onComplete(e, null);
                }
            }
            writeLock.unlock();
        });
    }
}
