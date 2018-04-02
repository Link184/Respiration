package com.google.firebase.database;

public class DatabaseReference {
    public interface CompletionListener {
        <T> void onComplete(Throwable var1, T var2);
    }
}
