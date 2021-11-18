package com.t0ugh.server.handler.impl.map;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({HDelHandlerTest.class, HExistsHandlerTest.class, HGetAllHandlerTest.class,
        HGetHandlerTest.class, HKeysHandlerTest.class, HLenHandlerTest.class,HSetHandlerTest.class})
public class MapHandlerTestSuite {
}
