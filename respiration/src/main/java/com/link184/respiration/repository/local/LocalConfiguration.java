package com.link184.respiration.repository.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class LocalConfiguration<T> {
    /** The name of a database file located in android files dir. */
    private String dbName = "respiration_db";
    /** Path to asset db asset file where it needs to be loaded. */
    private String assetDbFilePath;
    /** A path to node which you want to work with */
    private String[] databaseChildren;
    /** Model class */
    private Class<T> dataSnapshotType;

    public LocalConfiguration(Class<T> dataSnapshotType) {
        this.dataSnapshotType = dataSnapshotType;
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
