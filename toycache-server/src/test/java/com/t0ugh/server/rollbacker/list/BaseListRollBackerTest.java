package com.t0ugh.server.rollbacker.list;

import com.google.common.collect.Lists;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.After;
import org.junit.Before;

import java.util.List;

public class BaseListRollBackerTest extends BaseTest {
    @Before
    public void setUpBaseList() throws Exception {
        List<String> l = Lists.newArrayList();
        l.add("Hello");
        l.add("World");
        testContext.getStorage().backdoor().put("test", MemoryValueObject.newInstance(l));
    }

    @After
    public void tearDownBaseList() throws Exception {
    }
}
