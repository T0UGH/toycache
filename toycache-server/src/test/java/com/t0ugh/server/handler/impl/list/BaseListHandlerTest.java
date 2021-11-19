package com.t0ugh.server.handler.impl.list;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.Before;

import java.util.Map;

public class BaseListHandlerTest extends BaseTest {
    @Before
    public void setUpBaseListHandlerTest(){
        testContext.getStorage().backdoor().put("Hi", MemoryValueObject.newInstance(Lists.newArrayList("H1", "H2", "H3")));
    }
}
