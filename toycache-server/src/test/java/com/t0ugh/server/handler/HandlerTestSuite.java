package com.t0ugh.server.handler;

import com.t0ugh.server.handler.impl.control.RewriteLogHandlerTest;
import com.t0ugh.server.handler.impl.control.SaveHandlerTest;
import com.t0ugh.server.handler.impl.inner.InnerClearExpireTest;
import com.t0ugh.server.handler.impl.inner.InnerRewriteLogFinishHandlerTest;
import com.t0ugh.server.handler.impl.key.DelTest;
import com.t0ugh.server.handler.impl.key.ExistsTest;
import com.t0ugh.server.handler.impl.key.ExpireTest;
import com.t0ugh.server.handler.impl.list.ListHandlerTestSuite;
import com.t0ugh.server.handler.impl.map.MapHandlerTestSuite;
import com.t0ugh.server.handler.impl.set.SetHandlerTestSuite;
import com.t0ugh.server.handler.impl.sortedset.SortedSetHandlerTestSuite;
import com.t0ugh.server.handler.impl.string.GetTest;
import com.t0ugh.server.handler.impl.string.SetTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({InnerClearExpireTest.class, DelTest.class, InnerRewriteLogFinishHandlerTest.class,
        ExistsTest.class, ExpireTest.class, SaveHandlerTest.class,
        GetTest.class, SetTest.class, RewriteLogHandlerTest.class, MapHandlerTestSuite.class, ListHandlerTestSuite.class,
        SetHandlerTestSuite.class, SortedSetHandlerTestSuite.class})
public class HandlerTestSuite {
}
