package com.link184.respiration.repository.firebase;

import com.google.firebase.auth.FirebaseUser;
import com.link184.respiration.FirebaseRepository;

import java.util.regex.Pattern;

public class Configuration<T> {
    private boolean persistence;
    private boolean accessPrivate;
    private String databaseChildren;
    private Class<T> dataSnapshotType;
    private boolean childrenSensitive;
    private String userId;

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

    public String getDatabaseChildren(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseChildren = databaseChildren.replaceAll(Pattern.quote(com.link184.respiration.FirebaseRepository.USER_ID), userId);
        } else {
            databaseChildren = databaseChildren.replaceAll(Pattern.quote(userId), FirebaseRepository.USER_ID);
        }
        return databaseChildren;
    }

    public void setDatabaseChildren(String... databaseChildren) {
        StringBuilder sb = new StringBuilder();
        for (String child : databaseChildren) {
            if (child != null) {
                sb.append(child).append("/");
                if (child.equals(FirebaseRepository.USER_ID)) {
                    childrenSensitive = true;
                    userId = child;
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
