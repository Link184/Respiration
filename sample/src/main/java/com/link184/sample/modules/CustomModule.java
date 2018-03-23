package com.link184.sample.modules;

import com.link184.respiration.FirebaseRepository;
import com.link184.respiration.RespirationModule;
import com.link184.respiration.repository.firebase.FirebaseGeneralRepository;
import com.link184.sample.firebase.SamplePrivateModel;

import java.util.Vector;

/**
 * Created by jora on 11/25/17.
 */

@RespirationModule
public class CustomModule {
    public static final String SAMPLE_PRIVATE_CHILD = "private";
    public static final String SAMPLE_PUBLIC_CHILD = "public";
    public static final String SAMPLE_FRIENDS_CHILD = "friends";

    @FirebaseRepository(dataSnapshotType = SamplePrivateModel.class,
            isAccessPrivate = true,
            children = {SAMPLE_PRIVATE_CHILD, FirebaseRepository.USER_ID},
            persistence = true)
    public FirebaseGeneralRepository samplePrivateRepository;


    @FirebaseRepository(dataSnapshotType = Vector.class)
    public FirebaseGeneralRepository lalala;

    public String ignoreeee;

    public Long test() {
        return 22L;
    }
}
