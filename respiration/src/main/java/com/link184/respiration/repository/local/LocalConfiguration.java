package com.link184.respiration.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class LocalConfiguration<T> {
    /** Android context to work with asset resources. */
    private Context context;
    /** The name of a database file located in android files dir. */
    private String dbName = "respiration_db";
    /** Path to asset db asset file from where it needs to be loaded. */
    private String assetDbFilePath;
    /** A path to node which you want to work with */
    private String[] databaseChildren;
    /** Model class */
    private Class<T> dataSnapshotType;

    public LocalConfiguration(Class<T> dataSnapshotType) {
        this.dataSnapshotType = dataSnapshotType;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getAssetDbFilePath() {
        return assetDbFilePath;
    }

    public void setAssetDbFilePath(String assetDbFilePath) {
        String[] split = assetDbFilePath.split(Pattern.quote(File.separator));
        this.dbName = split[split.length - 1];
        this.assetDbFilePath = assetDbFilePath;
    }

    @Nullable
    public String[] getDatabaseChildren() {
        return databaseChildren;
    }

    public void setDatabaseChildren(@NonNull String... databaseChildren) {
        this.databaseChildren = databaseChildren;
    }

    public Class<T> getDataSnapshotType() {
        return dataSnapshotType;
    }
}
