package com.t0ugh.server.rollbacker.map;

import com.google.common.collect.Maps;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.After;
import org.junit.Before;

import java.util.Map;

public class BaseMapRollBackerTest extends BaseTest {

    @Before
    public void setUpBaseMap() throws Exception {
        Map<String, String> m = Maps.newHashMap();
        m.put("Hello", "111");
        m.put("World", "222");
        testContext.getStorage().backdoor().put("test", MemoryValueObject.newInstance(m));
    }

    @After
    public void tearDownBaseMap() throws Exception {
    }
}
