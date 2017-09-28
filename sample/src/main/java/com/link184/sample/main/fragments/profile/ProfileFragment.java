package com.link184.sample.main.fragments.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.link184.sample.R;
import com.link184.sample.firebase.SamplePrivateModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment implements ProfileView{
    @BindView(R.id.ageInputText) EditText age;
    @BindView(R.id.nameInputText) EditText name;
    @BindView(R.id.surnameInputText) EditText surname;

    private ProfilePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        presenter = new ProfilePresenter(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.attachView();
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @OnClick(R.id.btnUpdate)
    void updateClick(View view) {
        SamplePrivateModel samplePrivateModel = new SamplePrivateModel(name.getText().toString(),
                surname.getText().toString(), Integer.parseInt(age.getText().toString()));
        presenter.updateValue(samplePrivateModel);
    }

    @OnClick(R.id.btnLogout)
    void logoutClick(View view) {
        presenter.logout();
    }

    @Override
    public void newDataReceived(SamplePrivateModel privateModel) {
        name.setText(privateModel.getName());
        surname.setText(privateModel.getSurname());
        age.setText(String.valueOf(privateModel.getAge()));
    }

    @Override
    public Fragment getFragment() {
        return this;
    }
}
