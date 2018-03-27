package com.link184.sample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.link184.respiration.repository.local.LocalConfiguration;
import com.link184.respiration.repository.local.LocalListRepository;
import com.link184.respiration.repository.local.NotListableRepository;
import com.link184.respiration.subscribers.ListSubscriberRespiration;
import com.link184.sample.local.User;
import com.link184.sample.local.workout.Difficulty;
import com.link184.sample.local.workout.UserWorkout;
import com.link184.sample.main.SampleActivity;
import com.link184.sample.modules.LocalUserListRepositoryBuilder;
import com.link184.sample.modules.LocalWorkoutListRepositoryBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.observers.TestObserver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by eugeniu on 3/6/18.
 */

@RunWith(AndroidJUnit4.class)
public class LocalListRepositoryTest {
    private final String TAG = getClass().getSimpleName();
    private final String TEST_ASSET_USER_DB_NAME = "user_db.json";
    private final String TEST_ASSET_WORKOUT_DB_NAME = "workout_db.json";
    @Rule
    public ActivityTestRule<SampleActivity> activityTestRule = new ActivityTestRule<>(SampleActivity.class);
    private LocalListRepository<User> localUserListRepository;
    private LocalListRepository<UserWorkout> localWorkoutListRepository;

    @Before
    public void prepareRepository() {
        localUserListRepository = LocalUserListRepositoryBuilder.getInstance(activityTestRule.getActivity());
        localWorkoutListRepository = LocalWorkoutListRepositoryBuilder.getInstance(activityTestRule.getActivity());
    }

    @After
    public void resetDb() {
        resetRepositoriesFormAssets();
    }

    public <T> LocalListRepository<T> prepareRepository(Class<T> dataSnapShotType) {
        LocalConfiguration localConfiguration = new LocalConfiguration<>(dataSnapShotType);
        localConfiguration.setAssetDbFilePath(TEST_ASSET_USER_DB_NAME);
        localConfiguration.setDatabaseChildren("userData", "user");
        localConfiguration.setContext(activityTestRule.getActivity());
        return new LocalListRepository<>(localConfiguration);
    }

    private void resetRepositoriesFormAssets() {
        LocalConfiguration<User> localUserConfiguration = new LocalConfiguration<>(User.class);
        localUserConfiguration.setAssetDbFilePath(TEST_ASSET_USER_DB_NAME);
        localUserConfiguration.setDatabaseChildren("userData", "user");
        localUserConfiguration.setContext(activityTestRule.getActivity());
        localUserListRepository.resetRepository(localUserConfiguration, true);
        LocalConfiguration<UserWorkout> localWorkoutConfiguration = new LocalConfiguration<>(UserWorkout.class);
        localWorkoutConfiguration.setAssetDbFilePath(TEST_ASSET_WORKOUT_DB_NAME);
        localWorkoutConfiguration.setDatabaseChildren("user_workouts", "1");
        localWorkoutConfiguration.setContext(activityTestRule.getActivity());
        localWorkoutListRepository.resetRepository(localWorkoutConfiguration, true);
    }

    @Test
    public void testLocalListRepositoryWithWrongNonListableModel() throws Exception {
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
        localUserListRepository.subscribe(new ListSubscriberRespiration<User>() {
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
        TestObserver<UserWorkout> testObserver = new TestObserver<UserWorkout>() {
            @Override
            public void onNext(UserWorkout user) {
                assertNotNull(user);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
            }
        };
        localWorkoutListRepository.subscribe(new ListSubscriberRespiration<UserWorkout>() {
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


    @Test
    public void replaceListTest() throws Exception {
        int workoutId = 1;
        String workoutDescription = "testDescription";
        Difficulty workoutDifficulty = Difficulty.BEGINNER;
        String workoutStructure = "a structure";
        String workoutTitle = "test title";
        UserWorkout userWorkout = new UserWorkout(-workoutId, workoutDescription, workoutDifficulty, workoutStructure, workoutTitle);
        Map<String, UserWorkout> testMapToReplace = new HashMap<>();

        String testKey = "testKey";
        testMapToReplace.put(testKey, userWorkout);

        localWorkoutListRepository.setValue(testMapToReplace);
        TestObserver<UserWorkout> testObserver = new TestObserver<UserWorkout>() {
            @Override
            public void onNext(UserWorkout user) {
                Log.e(TAG, "onNext: " + user.toString());
                assertEquals(user, userWorkout);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
            }
        };

        localWorkoutListRepository.subscribe(new ListSubscriberRespiration<UserWorkout>() {
            @Override
            public void onSuccess(Map<String, UserWorkout> dataSnapShot) {
            }

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
