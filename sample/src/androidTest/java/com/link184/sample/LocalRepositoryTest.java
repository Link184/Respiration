package com.link184.sample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.link184.respiration.repository.local.GeneralLocalRepository;
import com.link184.respiration.repository.local.LocalConfiguration;
import com.link184.respiration.subscribers.SubscriberRespiration;
import com.link184.sample.main.SampleActivity;
import com.link184.sample.model.local.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import io.reactivex.observers.TestObserver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Ryzen on 3/2/2018.
 */

@RunWith(AndroidJUnit4.class)
public class LocalRepositoryTest {
    private final String TAG = getClass().getSimpleName();
    private final String TEST_ASSET_DB_NAME = "user_db.json";

    @Rule
    public ActivityTestRule<SampleActivity> activityTestRule = new ActivityTestRule<>(SampleActivity.class);
    private GeneralLocalRepository<User> generalLocalRepository;

    @Before
    public void prepareRepository() {
        LocalConfiguration localConfiguration = new LocalConfiguration<>(User.class);
        localConfiguration.setAssetDbFilePath(TEST_ASSET_DB_NAME);
        localConfiguration.setDatabaseChildren("userData", "user");
        generalLocalRepository = new GeneralLocalRepository<>(activityTestRule.getActivity(), localConfiguration);
    }

    private void resetRepositoryFormAssets() {
        LocalConfiguration localConfiguration = new LocalConfiguration<>(User.class);
        localConfiguration.setAssetDbFilePath(TEST_ASSET_DB_NAME);
        localConfiguration.setDatabaseChildren("userData", "user");
        File dbFile = new File(activityTestRule.getActivity().getFilesDir(), localConfiguration.getDbName());
        if (dbFile.exists()) {
            boolean deleted = dbFile.delete();
            assertTrue("Failed to remove test db file", deleted);
        }
        generalLocalRepository = new GeneralLocalRepository<>(activityTestRule.getActivity(), localConfiguration);
    }

    @Test
    public void localRepositoryTest() {
        resetRepositoryFormAssets();
        TestObserver<User> userTestObserver = new TestObserver<User>() {
            @Override
            public void onNext(User user) {
                assertNotNull("Repository emmit a null object", user != null);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        };
        generalLocalRepository.subscribe(userTestObserver::onNext, userTestObserver::onError, userTestObserver::onComplete);

        User value = generalLocalRepository.getValue();
        assertNotNull(value);
    }

    @Test
    public void dbWriteTest() throws InterruptedException {
        resetRepositoryFormAssets();
        float testRandomHeight = new Random(System.nanoTime()).nextFloat();
        User user = generalLocalRepository.getValue();
        assertNotNull(user);
        user.setHeight(testRandomHeight);
        generalLocalRepository.setValue(user);
        Thread.sleep(1_000);

        prepareRepository();

        TestObserver<User> userTestObserver = new TestObserver<User>() {
            @Override
            public void onNext(User user) {
                assertNotNull("Repository emmit a null object", user);
                assertEquals("Height are not set", user.getHeight(), testRandomHeight);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        };
        generalLocalRepository.subscribe(userTestObserver::onNext, userTestObserver::onError, userTestObserver::onComplete);
    }

    @Test
    public void dbDeletionTest() throws InterruptedException, IOException {
        resetRepositoryFormAssets();
        generalLocalRepository.removeValue();
        Thread.sleep(1_000);

        prepareRepository();

        TestObserver<User> userTestObserver = new TestObserver<User>() {
            @Override
            public void onNext(User user) {
                assertNull("Repository emmit a null object", user);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
                assertNotNull(t);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        };
        generalLocalRepository.subscribe(new SubscriberRespiration<User>() {
            @Override
            public void onSuccess(User dataSnapShot) {
                userTestObserver.onNext(dataSnapShot);
            }

            @Override
            public void onFailure(Throwable error) {
                userTestObserver.onError(error);
            }
        });
    }
}
