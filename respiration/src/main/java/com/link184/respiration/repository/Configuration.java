package com.link184.respiration.repository;

public class Configuration<T> {
    private String databaseChildren;
    private Class<T> dataSnapshotType;

    public Configuration(Class<T> dataSnapshotType, String... databaseChildren) {
        StringBuilder sb = new StringBuilder();
        for (String child : databaseChildren) {
            if (child != null) {
                sb.append(child).append("/");
            }
        }
        this.databaseChildren = sb.toString();
        this.dataSnapshotType = dataSnapshotType;
    }

    String getDatabaseChildren() {
        return databaseChildren;
    }

    Class<T> getDataSnapshotType() {
        return dataSnapshotType;
    }
}
