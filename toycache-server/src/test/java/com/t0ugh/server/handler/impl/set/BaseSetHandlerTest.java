package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Sets;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.Before;

public class BaseSetHandlerTest extends BaseTest {
    @Before
    public void setUpBaseSetHandlerTest() throws Exception {
        testContext.getStorage().backdoor().put("Hi", MemoryValueObject.newInstance(Sets.newHashSet("H1", "H2", "H3")));
    }
}
