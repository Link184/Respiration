package com.link184.respiration.repository;

/**
 * Created by eugeniu on 7/25/17.
 */

class FirebaseAuthenticationRequired extends Throwable {
    FirebaseAuthenticationRequired() {
        super("Must be signed in firebase.");
    }
}
