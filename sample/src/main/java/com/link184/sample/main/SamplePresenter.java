package com.link184.sample.main;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.link184.respiration.repository.firebase.FirebaseGeneralRepository;
import com.link184.respiration.subscribers.SubscriberRespiration;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.firebase.SamplePublicModel;
import com.link184.sample.firebase.dagger.FirebaseModule;
import com.link184.sample.main.fragments.FragmentState;

import javax.inject.Inject;

public class SamplePresenter {
    private final String TAG = getClass().getSimpleName();

    private SampleView view;
    @Inject
    FirebaseGeneralRepository<SamplePrivateModel> privateRepository;
    @Inject
    FirebaseGeneralRepository<SamplePublicModel> publicRepository;
    private SubscriberRespiration<SamplePublicModel> publicRepositorySubscriber;
    private FirebaseAuth.AuthStateListener authStateListener;

    public SamplePresenter(SampleView view) {
        this.view = view;
        ((SampleApplication) view.getContext().getApplication()).getAppComponent().inject(this);
    }

    void attachView() {
        publicRepositorySubscriber = new SubscriberRespiration<SamplePublicModel>() {
            @Override
            public void onSuccess(SamplePublicModel samplePublicModel) {

            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure: ", error);
            }
        };
        publicRepository.subscribe(publicRepositorySubscriber);

        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                privateRepository.resetRepository(FirebaseModule.SAMPLE_PRIVATE_CHILD,
                        firebaseAuth.getCurrentUser().getUid());
                view.navigateTo(FragmentState.PROFILE);
            } else {
                view.navigateTo(FragmentState.AUTHENTICATION);
            }
        };
        publicRepository.getFirebaseAuth().addAuthStateListener(authStateListener);
    }

    void detachView() {
        publicRepositorySubscriber.dispose();
        publicRepository.getFirebaseAuth().removeAuthStateListener(authStateListener);
    }

    boolean isUserAuthenticated() {
        return publicRepository.isUserAuthenticated();
    }
}
