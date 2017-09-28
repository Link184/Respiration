package com.link184.sample.main.fragments.registration;

import com.link184.respiration.repository.GeneralRepository;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SamplePublicModel;

import javax.inject.Inject;

/**
 * Created by erza on 9/28/17.
 */

public class RegistrationPresenter {
    private RegistrationView view;

    @Inject
    GeneralRepository<SamplePublicModel> publicRepository;

    RegistrationPresenter(RegistrationView registrationView) {
        this.view = registrationView;
        ((SampleApplication) view.getFragment().getActivity().getApplication())
                .getAppComponent().inject(this);
    }

    public void pushAccountToFirebase(String account, String password) {
        publicRepository.getFirebaseAuth().createUserWithEmailAndPassword(account, password);
    }
}
