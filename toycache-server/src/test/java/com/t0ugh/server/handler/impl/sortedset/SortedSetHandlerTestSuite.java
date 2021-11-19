package com.t0ugh.server.handler.impl.sortedset;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ZAddHandlerTest.class, ZCardHandlerTest.class, ZCountHandlerTest.class, ZRangeHandlerTest.class,
        ZRangeByScoreHandlerTest.class, ZRankHandlerTest.class, ZRemHandlerTest.class})
public class SortedSetHandlerTestSuite {
}
