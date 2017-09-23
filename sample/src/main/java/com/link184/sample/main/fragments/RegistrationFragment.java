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
import com.link184.sample.firebase.SamplePublicModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by erza on 9/22/17.
 */

public class RegistrationFragment extends Fragment {
    @BindView(R.id.accountInputText)
    EditText account;
    @BindView(R.id.passwordInput)
    EditText password;
    @BindView(R.id.confirmAccountInputText)
    EditText confirm;

    @Inject
    GeneralRepository<SamplePublicModel> publicRepository;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        ((SampleApplication) getActivity().getApplication()).getAppComponent().inject(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btnRegister)
    void registerClick(View view) {
        publicRepository.getFirebaseAuth().createUserWithEmailAndPassword(
                account.getText().toString(), password.getText().toString());
    }
}
