package com.t0ugh.server.rollbacker.set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.After;
import org.junit.Before;

import java.util.Map;
import java.util.Set;

public class BaseSetRollBackerTest extends BaseTest {

    @Before
    public void setUpBaseSet() throws Exception {
        Set<String> set = Sets.newHashSet("Hello", "World");
        testContext.getStorage().backdoor().put("test", MemoryValueObject.newInstance(set));
    }

    @After
    public void tearDownBaseSet() throws Exception {
    }
}
