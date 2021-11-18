package com.t0ugh.server.handler.impl.map;

import com.google.common.collect.Maps;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.Before;

import java.util.Map;

public class BaseMapHandlerTest extends BaseTest {

    @Before
    public void setUpBaseMapHandlerTest(){
        Map<String, String> m = Maps.newHashMap();
        m.put("Hello", "World");
        m.put("World", "World");
        testContext.getStorage().backdoor().put("Hi", MemoryValueObject.newInstance(m));
    }
}
