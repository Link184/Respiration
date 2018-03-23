package com.link184.sample.repository;

import android.util.Log;

import com.link184.respiration.FirebaseRepository;
import com.link184.respiration.repository.firebase.Configuration;
import com.link184.respiration.repository.firebase.FirebaseGeneralRepository;
import com.link184.sample.firebase.SamplePrivateModel;

/**
 * Created by jora on 11/12/17.
 */

@FirebaseRepository(dataSnapshotType = SamplePrivateModel.class,
        children = {"children1", "child2", FirebaseRepository.USER_ID, "child3"})
public class CustomRepositoryFirebase extends FirebaseGeneralRepository<SamplePrivateModel> {
    public CustomRepositoryFirebase(Configuration<SamplePrivateModel> repositoryConfig) {
        super(repositoryConfig);
    }

    public void testMethod() {
        Log.d(TAG, "testMethod: ");
    }
}
