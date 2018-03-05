package com.link184.respiration.repository.local;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.link184.respiration.repository.base.Repository;
import com.link184.respiration.utils.Preconditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eugeniu on 3/2/18.
 */

abstract class LocalRepository<T> extends Repository<T> {
    private static final String DB_NAME = "respiration_db";
    protected static JsonElement rawJsonElement;
    protected final String[] databaseChildren;

    LocalRepository(Context context, LocalConfiguration localConfiguration) {
        File file = new File(context.getFilesDir(), DB_NAME);
        if (rawJsonElement == null) {
            if (file.exists()) {
                rawJsonElement = loadJsonFile(context);
            } else {
                loadFromAssets(context, localConfiguration.getAssetDbFilePath());
                rawJsonElement = loadJsonFile(context);
            }
        }
        this.databaseChildren = Preconditions.checkNotNull(localConfiguration.getDatabaseChildren());
        this.dataSnapshotClass = localConfiguration.getDataSnapshotType();
        initRepository();
    }

    @Nullable
    private JsonElement loadJsonFile(Context context) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(new File(context.getFilesDir(), DB_NAME)));
            return gson.toJsonTree(gson.fromJson(reader, Object.class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load json file template from assets and copy it to androids files dir.
     *
     * @param context  android context
     * @param filePath
     * @return
     */
    @Nullable
    private JsonElement loadFromAssets(Context context, String filePath) {
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            File file = new File(context.getFilesDir(), DB_NAME);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return loadJsonFile(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
