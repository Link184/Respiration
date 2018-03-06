package com.link184.respiration;

import com.link184.respiration.repository.local.LocalConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import static junit.framework.Assert.assertEquals;

/**
 * Created by eugeniu on 3/6/18.
 */

@RunWith(JUnit4.class)
public class ConfigurationTest {
    @Test
    public void testLocalConfiguration() throws Exception {
        final String folderName = "folderName";
        final String fileName  = "fileName.json";
        LocalConfiguration<Object> localConfiguration = new LocalConfiguration<>(Object.class);
        localConfiguration.setAssetDbFilePath(folderName + File.separator + fileName);
        assertEquals(fileName, localConfiguration.getDbName());

        localConfiguration.setAssetDbFilePath(fileName);
        assertEquals(fileName, localConfiguration.getDbName());

        localConfiguration.setAssetDbFilePath(File.separator + folderName + File.separator + fileName);
        assertEquals(fileName, localConfiguration.getDbName());

        localConfiguration.setAssetDbFilePath(folderName + "/" + fileName);
        assertEquals(fileName, localConfiguration.getDbName());
    }
}
