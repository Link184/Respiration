package com.link184.sample.main.fragments.profile;

import android.util.Log;

import com.link184.respiration.repository.firebase.FirebaseListRepository;
import com.link184.respiration.subscribers.ListSubscriberRespiration;
import com.link184.respiration.subscribers.SingleSubscriberRespiration;
import com.link184.respiration.subscribers.SubscriberRespiration;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SampleFriendModel;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.modules.RespirationCustomModule;
import com.link184.sample.modules.SamplePrivateRepository;

import java.util.List;

import javax.inject.Inject;

public class ProfilePresenter {
    private final String TAG = getClass().getSimpleName();
    private ProfileView view;
    private SubscriberRespiration<SamplePrivateModel> privateRepositorySubscriber;

//    @Inject
//    FirebaseGeneralRepository<SamplePrivateModel> privateRepository;
    SamplePrivateRepository privateRepository = RespirationCustomModule.getSamplePrivateRepository();
    @Inject
    FirebaseListRepository<SampleFriendModel> firebaseListRepository;

    private SingleSubscriberRespiration<List<SampleFriendModel>> listRepositorySubscriber;
    private SubscriberRespiration<SampleFriendModel> friendSubscriber;

    ProfilePresenter(ProfileView view) {
        this.view = view;
        ((SampleApplication) view.getFragment().getActivity().getApplication())
                .getAppComponent().inject(this);
    }

    void attachView() {
        privateRepositorySubscriber = new SubscriberRespiration<SamplePrivateModel>() {
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

        listRepositorySubscriber = new SingleSubscriberRespiration<List<SampleFriendModel>>() {
            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure:1 ", error);
            }

            @Override
            public void onSuccess(List<SampleFriendModel> dataSnapShot) {
                for (SampleFriendModel friend : dataSnapShot) {
                    Log.e(TAG, "onSuccess:1 " + friend.toString());
                }
            }
        };
        firebaseListRepository.subscribeToList(listRepositorySubscriber);

        firebaseListRepository.subscribe(new ListSubscriberRespiration<SampleFriendModel>() {
            @Override
            public void onReceive(String key, SampleFriendModel value) {
                Log.e(TAG, "onSuccess:2 " + key + " " + value.toString());
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure:2 ", error);
            }
        });

        friendSubscriber = new SubscriberRespiration<SampleFriendModel>() {
            @Override
            public void onSuccess(SampleFriendModel dataSnapShot) {
                Log.e(TAG, "onSuccess: " + dataSnapShot.toString());
            }
        };
        firebaseListRepository.subscribeToItem("john", friendSubscriber);
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
