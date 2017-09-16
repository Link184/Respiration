package com.link184.sample.firebase.dagger;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.link184.respiration.repository.GeneralRepository;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.firebase.SamplePublicModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {
    public static final String SAMPLE_PRIVATE_CHILD = "private";
    public static final String SAMPLE_PUBLIC_CHILD = "public";

    @Provides
    @Singleton
    public GeneralRepository<SamplePublicModel> providesSamplePublicRepository() {
        return new GeneralRepository.Builder<>(SamplePublicModel.class, SAMPLE_PUBLIC_CHILD)
                .setPersistence(true)
                .build();
    }

    @Provides
    @Singleton
    public GeneralRepository<SamplePrivateModel> providesSamplePrivateRepository() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return new GeneralRepository.Builder<>(SamplePrivateModel.class,
                SAMPLE_PRIVATE_CHILD, currentUser != null ? currentUser.getUid() : null)
                .setAccessPrivate(true)
                .setPersistence(true)
                .build();
    }
}
