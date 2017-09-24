package com.link184.sample.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.link184.respiration.repository.GeneralRepository;
import com.link184.respiration.repository.ListRepository;
import com.link184.respiration.subscribers.SingleSubscriberFirebase;
import com.link184.respiration.subscribers.SubscriberFirebase;
import com.link184.sample.R;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SampleFriendModel;
import com.link184.sample.firebase.SamplePrivateModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by erza on 9/22/17.
 */

public class ProfileFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.ageInputText) EditText age;
    @BindView(R.id.nameInputText) EditText name;
    @BindView(R.id.surnameInputText) EditText surname;

    @Inject
    GeneralRepository<SamplePrivateModel> privateRepository;
    private SubscriberFirebase<SamplePrivateModel> privateRepositorySubscriber;
    @Inject
    ListRepository<SampleFriendModel> listRepository;
    private SingleSubscriberFirebase<List<SampleFriendModel>> listRepositorySubscriber;
    private SubscriberFirebase<SampleFriendModel> friendSubscriber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        ((SampleApplication) getActivity().getApplication()).getAppComponent().inject(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        privateRepositorySubscriber = new SubscriberFirebase<SamplePrivateModel>() {
            @Override
            public void onSuccess(SamplePrivateModel samplePrivateModel) {
                name.setText(samplePrivateModel.getName());
                surname.setText(samplePrivateModel.getSurname());
                age.setText(String.valueOf(samplePrivateModel.getAge()));
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

    @Override
    public void onDestroyView() {
        privateRepositorySubscriber.dispose();
        friendSubscriber.dispose();
        super.onDestroyView();
    }

    @OnClick(R.id.btnUpdate)
    void updateClick(View view) {
        SamplePrivateModel samplePrivateModel = new SamplePrivateModel(name.getText().toString(),
                surname.getText().toString(), Integer.parseInt(age.getText().toString()));
        privateRepository.setValue(samplePrivateModel);
    }

    @OnClick(R.id.btnLogout)
    void logoutClick(View view) {
        privateRepository.getFirebaseAuth().signOut();
    }
}
