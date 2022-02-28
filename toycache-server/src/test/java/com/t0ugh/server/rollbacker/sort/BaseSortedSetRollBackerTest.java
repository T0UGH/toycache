package com.t0ugh.server.rollbacker.sort;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryComparableString;
import com.t0ugh.server.storage.MemorySortedSet;
import com.t0ugh.server.storage.MemoryValueObject;
import org.junit.After;
import org.junit.Before;

import java.util.Set;

public class BaseSortedSetRollBackerTest extends BaseTest {

    @Before
    public void setUpBaseSortedSet() throws Exception {
        MemorySortedSet mss = new MemorySortedSet();
        mss.addAll(Lists.newArrayList(new MemoryComparableString("H1", 5),
                new MemoryComparableString("H2", 3),
                new MemoryComparableString("H3", 1)));
        testContext.getStorage().backdoor().put("Hi", MemoryValueObject.newInstance(mss));
    }

    @After
    public void tearDownBaseSortedSet() throws Exception {
    }
}
