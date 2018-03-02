package com.link184.respiration.suite;

import com.link184.respiration.FirebaseRepositoryTest;
import com.link184.respiration.UtilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Ryzen on 2/7/2018.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({UtilsTest.class, FirebaseRepositoryTest.class})
public class UnitTestSuite {
}
