package com.link184.respiration.repository.local;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class LocalConfiguration {
    private String dbFileName;
    private String assetDbFilePath;

    public String getDbFileName() {
        return dbFileName;
    }

    public void setDbFileName(String dbFileName) {
        this.dbFileName = dbFileName;
    }

    public String getAssetDbFilePath() {
        return assetDbFilePath;
    }

    public void setAssetDbFilePath(String assetDbFilePath) {
        this.assetDbFilePath = assetDbFilePath;
    }
}
