package com.link184.sample.main.fragments.profile;

import android.util.Log;

import com.link184.respiration.repository.GeneralRepository;
import com.link184.respiration.repository.ListRepository;
import com.link184.respiration.subscribers.SingleSubscriberFirebase;
import com.link184.respiration.subscribers.SubscriberFirebase;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SampleFriendModel;
import com.link184.sample.firebase.SamplePrivateModel;

import java.util.List;

import javax.inject.Inject;

public class ProfilePresenter {
    private final String TAG = getClass().getSimpleName();
    private ProfileView view;
    private SubscriberFirebase<SamplePrivateModel> privateRepositorySubscriber;

    @Inject
    GeneralRepository<SamplePrivateModel> privateRepository;
    @Inject
    ListRepository<SampleFriendModel> listRepository;

    private SingleSubscriberFirebase<List<SampleFriendModel>> listRepositorySubscriber;
    private SubscriberFirebase<SampleFriendModel> friendSubscriber;

    ProfilePresenter(ProfileView view) {
        this.view = view;
        ((SampleApplication) view.getFragment().getActivity().getApplication())
                .getAppComponent().inject(this);
    }

    void attachView() {
        privateRepositorySubscriber = new SubscriberFirebase<SamplePrivateModel>() {
            @Override
            public void onSuccess(SamplePrivateModel samplePrivateModel) {
                view.newDataReceived(samplePrivateModel);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure: ", error);
            }
        };
        privateRepository.subscribe(privateRepositorySubscriber);

        listRepositorySubscriber = new SingleSubscriberFirebase<List<SampleFriendModel>>() {
            @Override
            public void onSuccess(List<SampleFriendModel> dataSnapShot) {
                for (SampleFriendModel friend : dataSnapShot) {
                    Log.e(TAG, "onSuccess: " + friend.toString());
                }
            }
        };
        listRepository.subscribeToList(listRepositorySubscriber);

        friendSubscriber = new SubscriberFirebase<SampleFriendModel>() {
            @Override
            public void onSuccess(SampleFriendModel dataSnapShot) {
                Log.e(TAG, "onSuccess: " + dataSnapShot.toString());
            }
        };
        listRepository.subscribeToItem("john", friendSubscriber);
    }

    void detachView() {
        privateRepositorySubscriber.dispose();
        friendSubscriber.dispose();
    }

    void updateValue(SamplePrivateModel samplePrivateModel) {
        privateRepository.setValue(samplePrivateModel);
    }

    void logout() {
        privateRepository.getFirebaseAuth().signOut();
    }
}
