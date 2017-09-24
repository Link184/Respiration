package com.link184.respiration.repository;

public class Configuration<T> {
    private boolean persistence;
    private boolean accessPrivate;
    private String databaseChildren;
    private Class<T> dataSnapshotType;

    public Configuration(Class<T> dataSnapshotType) {
        this.dataSnapshotType = dataSnapshotType;
    }

    boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    boolean isAccessPrivate() {
        return accessPrivate;
    }

    public void setAccessPrivate(boolean accessPrivate) {
        this.accessPrivate = accessPrivate;
    }

    String getDatabaseChildren() {
        return databaseChildren;
    }

    public void setDatabaseChildren(String... databaseChildren) {
        StringBuilder sb = new StringBuilder();
        for (String child : databaseChildren) {
            if (child != null) {
                sb.append(child).append("/");
            }
        }
        this.databaseChildren = sb.toString();
    }

    Class<T> getDataSnapshotType() {
        return dataSnapshotType;
    }

    public void setDataSnapshotType(Class<T> dataSnapshotType) {
        this.dataSnapshotType = dataSnapshotType;
    }
}
