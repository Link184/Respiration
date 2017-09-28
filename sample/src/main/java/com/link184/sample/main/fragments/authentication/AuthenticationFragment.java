package com.link184.sample.main.fragments.authentication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.link184.sample.R;
import com.link184.sample.main.SampleActivity;
import com.link184.sample.main.fragments.FragmentState;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by erza on 9/22/17.
 */

public class AuthenticationFragment extends Fragment implements AuthenticationView{
    @BindView(R.id.loginInputText) EditText login;
    @BindView(R.id.passwordInputText) EditText password;

    private AuthenticationPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new AuthenticationPresenter(this);
    }

    @OnClick(R.id.btnLogin)
    void loginClick(View view) {
        presenter.performLogin(login.getText().toString(), password.getText().toString(), task -> {
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

    @Override
    public Fragment getFragment() {
        return this;
    }
}
