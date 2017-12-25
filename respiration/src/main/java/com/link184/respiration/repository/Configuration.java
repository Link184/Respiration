package com.link184.respiration.repository;

import com.link184.respiration.RespirationRepository;

import java.util.regex.Pattern;

public class Configuration<T> {
    private boolean persistence;
    private boolean accessPrivate;
    private String databaseChildren;
    private Class<T> dataSnapshotType;
    private boolean childrenSensitive;

    public Configuration(Class<T> dataSnapshotType) {
        this.dataSnapshotType = dataSnapshotType;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    public boolean isAccessPrivate() {
        return accessPrivate;
    }

    public void setAccessPrivate(boolean accessPrivate) {
        this.accessPrivate = accessPrivate;
    }

    public String getDatabaseChildren() {
        return databaseChildren;
    }

    public String getDatabaseChildren(String userId) {
        databaseChildren = databaseChildren.replaceAll(Pattern.quote(RespirationRepository.USER_ID), userId);
        return databaseChildren;
    }

    public void setDatabaseChildren(String... databaseChildren) {
        StringBuilder sb = new StringBuilder();
        for (String child : databaseChildren) {
            if (child != null) {
                sb.append(child).append("/");
                if (child.equals(RespirationRepository.USER_ID)) {
                    childrenSensitive = true;
                }
            }
        }
        this.databaseChildren = sb.toString();
    }

    public Class<T> getDataSnapshotType() {
        return dataSnapshotType;
    }

    public void setDataSnapshotType(Class<T> dataSnapshotType) {
        this.dataSnapshotType = dataSnapshotType;
    }

    public boolean isChildrenSensitive() {
        return childrenSensitive;
    }
}
