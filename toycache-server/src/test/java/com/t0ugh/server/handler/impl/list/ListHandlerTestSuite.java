package com.t0ugh.server.handler.impl.list;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({LIndexHandlerTest.class, LLenHandlerTest.class, LPopHandlerTest.class, LPushHandlerTest.class,
    LRangeHandlerTest.class, LSetHandlerTest.class, LTrimHandlerTest.class})
public class ListHandlerTestSuite {
}
