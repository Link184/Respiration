package com.link184.sample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.link184.respiration.repository.local.ListLocalRepository;
import com.link184.respiration.repository.local.LocalConfiguration;
import com.link184.respiration.repository.local.NotListableRepository;
import com.link184.respiration.subscribers.ListSubscriberRespiration;
import com.link184.sample.main.SampleActivity;
import com.link184.sample.model.local.User;
import com.link184.sample.model.local.workout.UserWorkout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import io.reactivex.observers.TestObserver;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by eugeniu on 3/6/18.
 */

@RunWith(AndroidJUnit4.class)
public class ListLocalRepositoryTest {
    private final String TAG = getClass().getSimpleName();
    private final String TEST_ASSET_USER_DB_NAME = "user_db.json";
    private final String TEST_ASSET_WORKOUT_DB_NAME = "workout_db.json";
    @Rule
    public ActivityTestRule<SampleActivity> activityTestRule = new ActivityTestRule<>(SampleActivity.class);

    public <T> ListLocalRepository<T> prepareRepository(Class<T> dataSnapShotType) {
        LocalConfiguration localConfiguration = new LocalConfiguration<>(dataSnapShotType);
        localConfiguration.setAssetDbFilePath(TEST_ASSET_USER_DB_NAME);
        localConfiguration.setDatabaseChildren("userData", "user");
        return new ListLocalRepository<>(activityTestRule.getActivity(), localConfiguration);
    }

    private <T> ListLocalRepository<T> resetRepositoryFormAssets(Class<T> dataSnapShotType, String dbAssetPath) {
        LocalConfiguration localConfiguration = new LocalConfiguration<>(dataSnapShotType);
        localConfiguration.setAssetDbFilePath(dbAssetPath);
        localConfiguration.setDatabaseChildren("userData", "user");
        File dbFile = new File(activityTestRule.getActivity().getFilesDir(), localConfiguration.getDbName());
        if (dbFile.exists()) {
            boolean deleted = dbFile.delete();
            assertTrue("Failed to remove test db file", deleted);
        }
        return new ListLocalRepository<>(activityTestRule.getActivity(), localConfiguration);
    }

    @Test
    public void testLocalListRepositoryWithWrongNonListableModel() throws Exception {
        ListLocalRepository<User> listLocalRepository = resetRepositoryFormAssets(User.class, TEST_ASSET_USER_DB_NAME);
        TestObserver<User> testObserver = new TestObserver<User>() {
            @Override
            public void onNext(User user) {
                assertNever(user);
            }

            @Override
            public void onError(Throwable t) {
                assertTrue("There are another error instead of the expected one", t instanceof NotListableRepository);
            }
        };
        listLocalRepository.subscribe(new ListSubscriberRespiration<User>() {
            @Override
            public void onReceive(String key, User value) {
                testObserver.onNext(value);
            }

            @Override
            public void onFailure(Throwable error) {
                testObserver.onError(error);
            }
        });
    }

    @Test
    public void testLocalListRepositoryWithRightModel() throws Exception {
        ListLocalRepository<UserWorkout> listLocalRepository = resetRepositoryFormAssets(UserWorkout.class, TEST_ASSET_WORKOUT_DB_NAME);
        TestObserver<UserWorkout> testObserver = new TestObserver<UserWorkout>() {
            @Override
            public void onNext(UserWorkout user) {
                Log.e(TAG, "onNext: " + user.toString());
                assertNull(user);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
            }
        };
        listLocalRepository.subscribe(new ListSubscriberRespiration<UserWorkout>() {
            @Override
            public void onReceive(String key, UserWorkout value) {
                testObserver.onNext(value);
            }

            @Override
            public void onFailure(Throwable error) {
                testObserver.onError(error);
            }
        });
    }
}
