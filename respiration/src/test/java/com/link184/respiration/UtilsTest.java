package com.link184.respiration;


import com.link184.respiration.models.TestModel;
import com.link184.respiration.utils.RespirationUtils;
import com.link184.respiration.utils.mapper.Mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Notification;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by Ryzen on 2/7/2018.
 */

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {
    @Test
    public void mapTest() {
        TestModel testModel = mock(TestModel.class);
        Mapper<TestModel, String> stringMapper = source -> source.getName() + source.getName() + source.getAlias();
        String mappedResult = RespirationUtils.mapItem(testModel, stringMapper);
        assertNotNull(mappedResult);
    }

    @Test
    public void mapToListTest() throws Exception {
        Map<String, TestModel> map = new HashMap<>();
        map.put("model1", new TestModel("name1", 12, "alias1"));
        map.put("model2", new TestModel("name2", 14, "alias2"));
        map.put("model3", new TestModel("name3", 15, "alias3"));
        map.put("model4", null);
        Notification<Map<String, TestModel>> notification = Notification.createOnNext(map);
        Notification<List<TestModel>> listNotification = RespirationUtils.mapToList(notification);
        assertNotNull("Result notification are null", listNotification);
        List<TestModel> resultList = listNotification.getValue();
        assertTrue("Result list size and source map size are not the same", resultList.size() == map.size());
    }
}
