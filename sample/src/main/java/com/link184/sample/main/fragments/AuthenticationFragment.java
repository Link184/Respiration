package com.link184.sample.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.link184.respiration.repository.GeneralRepository;
import com.link184.sample.R;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.main.SampleActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by erza on 9/22/17.
 */

public class AuthenticationFragment extends Fragment {
    @BindView(R.id.loginInputText) EditText login;
    @BindView(R.id.passwordInputText) EditText password;

    @Inject
    GeneralRepository<SamplePrivateModel> privateRepository;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        ButterKnife.bind(this, view);
        ((SampleApplication) getActivity().getApplication()).getAppComponent().inject(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btnLogin)
    void loginClick(View view) {
        privateRepository.getFirebaseAuth()
                .signInWithEmailAndPassword(login.getText().toString(), password.getText().toString())
                .addOnCompleteListener(getActivity(), task -> {
                    if (!task.isSuccessful()) {
                        login.setError("Check failed");
                        password.setError("Check failed");
                    }
                });
    }

    @OnClick(R.id.btnRegister)
    void registerClick(View v) {
        ((SampleActivity) getActivity()).navigateTo(FragmentState.REGISTRATION);
    }
}
