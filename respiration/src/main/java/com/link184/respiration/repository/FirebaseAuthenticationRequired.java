package com.link184.respiration.repository;

public class FirebaseAuthenticationRequired extends Throwable {
    FirebaseAuthenticationRequired() {
        super("Must be signed in firebase.");
    }
}
