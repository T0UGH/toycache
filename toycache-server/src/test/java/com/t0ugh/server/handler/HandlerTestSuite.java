package com.t0ugh.server.handler;

import com.t0ugh.server.handler.impl.inner.InnerClearExpireTest;
import com.t0ugh.server.handler.impl.key.DelTest;
import com.t0ugh.server.handler.impl.key.ExistsTest;
import com.t0ugh.server.handler.impl.key.ExpireTest;
import com.t0ugh.server.handler.impl.string.GetTest;
import com.t0ugh.server.handler.impl.string.SetTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({InnerClearExpireTest.class, DelTest.class,
        ExistsTest.class, ExpireTest.class,
        GetTest.class, SetTest.class})
public class HandlerTestSuite {
}
