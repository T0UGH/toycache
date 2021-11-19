package com.t0ugh.server.handler.impl.sortedset;

import com.google.common.collect.Lists;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryComparableString;
import com.t0ugh.server.storage.MemorySortedSet;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.Before;

public class BaseSortedSetHandlerTest extends BaseTest {
    @Before
    public void setUpBaseSortedSetHandlerTest() throws Exception {
        MemorySortedSet mss = new MemorySortedSet();
        mss.addAll(Lists.newArrayList(new MemoryComparableString("H1", 5),
                new MemoryComparableString("H2", 3),
                new MemoryComparableString("H3", 1)));
        testContext.getStorage().backdoor().put("Hi", MemoryValueObject.newInstance(mss));
    }
}
