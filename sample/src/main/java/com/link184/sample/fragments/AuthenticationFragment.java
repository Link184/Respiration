package com.link184.sample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.link184.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by erza on 9/22/17.
 */

public class AuthenticationFragment extends Fragment {
    @BindView(R.id.loginInputText)
    EditText login;
    @BindView(R.id.passwordInputText)
    EditText password;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btnLogin)
    void loginClick(View view) {
    }


}
