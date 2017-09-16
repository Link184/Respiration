package com.link184.sample.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.link184.respiration.repository.GeneralRepository;
import com.link184.respiration.subscribers.SubscriberFirebase;
import com.link184.sample.R;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.firebase.SamplePublicModel;
import com.link184.sample.firebase.dagger.FirebaseModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SampleActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    @Inject
    GeneralRepository<SamplePrivateModel> privateRepository;
    @Inject
    GeneralRepository<SamplePublicModel> publicRepository;

    @BindView(R.id.idName) TextView name;
    @BindView(R.id.idAge) TextView age;
    @BindView(R.id.idSurname) TextView surname;

    @BindView(R.id.temperatureLabel) TextView temperature;
    @BindView(R.id.humidityLabel) TextView humidity;
    private SubscriberFirebase<SamplePublicModel> publicRepositorySubscriber;
    private SubscriberFirebase<SamplePrivateModel> privateRepositorySubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        ButterKnife.bind(this);

        ((SampleApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        publicRepositorySubscriber = new SubscriberFirebase<SamplePublicModel>() {
            @Override
            public void onSuccess(SamplePublicModel samplePublicModel) {
                temperature.setText(getString(R.string.temperature, samplePublicModel.getTemperature()));
                humidity.setText(getString(R.string.humidity, samplePublicModel.getHumidity()));
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure: ", error);
            }
        };

        privateRepositorySubscriber = new SubscriberFirebase<SamplePrivateModel>() {
            @Override
            public void onSuccess(SamplePrivateModel samplePrivateModel) {
                name.setVisibility(View.VISIBLE);
                age.setVisibility(View.VISIBLE);
                surname.setVisibility(View.VISIBLE);

                name.setText(samplePrivateModel.getName());
                age.setText(String.valueOf(samplePrivateModel.getAge()));
                surname.setText(samplePrivateModel.getSurname());
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure: ", error);
                name.setVisibility(View.GONE);
                age.setVisibility(View.GONE);
                surname.setVisibility(View.GONE);
            }
        };

        privateRepository.subscribe(privateRepositorySubscriber);
        publicRepository.subscribe(publicRepositorySubscriber);
    }

    @Override
    protected void onPause() {
        privateRepositorySubscriber.dispose();
        publicRepositorySubscriber.dispose();
        super.onPause();
    }

    @OnClick(R.id.btnSave)
    void saveClicked(View v) {
        SamplePrivateModel samplePrivateModel = new SamplePrivateModel(name.getText().toString(),
                surname.getText().toString(), Integer.parseInt(age.getText().toString()));
        privateRepository.setValue(samplePrivateModel);
    }

    @OnClick(R.id.btnLogin)
    void loginClicked(View v) {
        privateRepository.getFirebaseAuth()
                .signInWithEmailAndPassword("sample@sample.sample", "123123")
        .addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                privateRepository.resetRepository(FirebaseModule.SAMPLE_PRIVATE_CHILD,
                        privateRepository.getFirebaseAuth().getCurrentUser().getUid());
            }
        });
    }

    @OnClick(R.id.btnLogout)
    void logoutClicked(View v) {
        privateRepository.getFirebaseAuth().signOut();
    }
}
