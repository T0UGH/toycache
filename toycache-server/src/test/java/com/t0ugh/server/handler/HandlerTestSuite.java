package com.t0ugh.server.handler;

import com.t0ugh.server.handler.impl.inner.InnerClearExpireHandlerTest;
import com.t0ugh.server.handler.impl.key.DelHandlerTest;
import com.t0ugh.server.handler.impl.key.ExistsHandlerTest;
import com.t0ugh.server.handler.impl.key.ExpireHandlerTest;
import com.t0ugh.server.handler.impl.string.GetHandlerTest;
import com.t0ugh.server.handler.impl.string.SetHandlerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({InnerClearExpireHandlerTest.class, DelHandlerTest.class,
        ExistsHandlerTest.class, ExpireHandlerTest.class,
        GetHandlerTest.class, SetHandlerTest.class})
public class HandlerTestSuite {
}
