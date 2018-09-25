package com.link184.sample;

import com.link184.respiration.utils.IdGenerator;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.espresso.core.deps.guava.collect.Ordering;
import androidx.test.runner.AndroidJUnit4;

import static junit.framework.Assert.assertTrue;

/**
 * Created by eugeniu on 3/6/18.
 */

@RunWith(AndroidJUnit4.class)
public class IdGeneratorTest {
    @Test
    public void testIdOrder() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ids.add(IdGenerator.generate());
        }
        assertTrue(Ordering.natural().isOrdered(ids));
    }
}
