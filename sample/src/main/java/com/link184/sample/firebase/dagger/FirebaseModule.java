package com.link184.sample.firebase.dagger;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.link184.respiration.repository.firebase.FirebaseGeneralRepository;
import com.link184.respiration.repository.firebase.FirebaseListRepository;
import com.link184.sample.firebase.SampleFriendModel;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.firebase.SamplePublicModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {
    public static final String SAMPLE_PRIVATE_CHILD = "private";
    public static final String SAMPLE_PUBLIC_CHILD = "public";
    public static final String SAMPLE_FRIENDS_CHILD = "friends";

    @Provides
    @Singleton
    public FirebaseGeneralRepository<SamplePublicModel> providesSamplePublicRepository() {
        return new FirebaseGeneralRepository.Builder<>(SamplePublicModel.class)
                .setChildren(SAMPLE_PUBLIC_CHILD)
                .setPersistence(true)
                .build();
    }

    @Provides
    @Singleton
    public FirebaseGeneralRepository<SamplePrivateModel> providesSamplePrivateRepository() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return new FirebaseGeneralRepository.Builder<>(SamplePrivateModel.class)
                .setChildren(SAMPLE_PRIVATE_CHILD, currentUser != null ? currentUser.getUid() : null)
                .setAccessPrivate(true)
                .setPersistence(true)
                .build();
    }

    @Provides
    @Singleton
    public FirebaseListRepository<SampleFriendModel> providesFriendsRepository() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return new FirebaseListRepository.Builder<>(SampleFriendModel.class)
                .setChildren(SAMPLE_FRIENDS_CHILD, currentUser != null ? currentUser.getUid() : null)
                .setAccessPrivate(true)
                .setPersistence(true)
                .build();
    }
}
