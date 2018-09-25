package com.link184.sample.main.fragments.authentication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.link184.respiration.repository.firebase.FirebaseGeneralRepository;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SamplePrivateModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class AuthenticationPresenter {
    private AuthenticationView view;

    @Inject
    FirebaseGeneralRepository<SamplePrivateModel> privateRepository;

    public AuthenticationPresenter(AuthenticationView view) {
        this.view = view;
        ((SampleApplication) view.getFragment().getActivity().getApplication())
                .getAppComponent().inject(this);
    }

    public void performLogin(String login, String password, @NonNull OnCompleteListener completeListener) {
        privateRepository.getFirebaseAuth()
                .signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(view.getFragment().getActivity(), completeListener);
    }
}
