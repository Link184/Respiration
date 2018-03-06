package com.link184.sample;

import com.link184.respiration.repository.local.ListLocalRepository;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by eugeniu on 3/6/18.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({IdGeneratorTest.class, LocalRepositoryTest.class, ListLocalRepository.class})
public class GeneralTestSuite {
}
