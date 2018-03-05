package com.link184.respiration.repository.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class LocalConfiguration {
    private String assetDbFilePath;
    private String[] databaseChildren;

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
}
