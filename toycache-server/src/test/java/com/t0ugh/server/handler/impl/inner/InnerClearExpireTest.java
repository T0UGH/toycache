package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.config.Config;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.ExpireMap;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InnerClearExpireTest extends BaseTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClearExpireKey() throws Exception {
        testContext.getStorage().set("Hello", "World");
        testContext.getStorage().set("Hi", "World");
        testContext.getStorage().set("Haha", "World");
        testContext.getExpireMap().backdoor().put("Hello", 1636613116992L);
        testContext.getExpireMap().backdoor().put("Hi", System.currentTimeMillis() + 10000000L);

        Proto.Response resp = callHandler(testContext);
        TestUtils.assertOK(Proto.MessageType.InnerClearExpire, resp);
        assertEquals(1, resp.getInnerClearExpireResponse().getCleared());
        Storage storage = testContext.getStorage();
        ExpireMap em = testContext.getExpireMap();
        assertEquals(2, storage.backdoor().size());
        assertTrue(storage.backdoor().containsKey("Hi"));
        assertTrue(storage.backdoor().containsKey("Haha"));
        assertFalse(storage.backdoor().containsKey("Hello"));
        assertEquals(1, em.backdoor().size());
        assertTrue(em.backdoor().containsKey("Hi"));
        assertFalse(em.backdoor().containsKey("Hello"));
    }

    /**
     * 测试按照配置文件中设置的 {@link Config#getUpperKeyLimitOfPeriodicalDelete()} 属性
     * 至多只能删除这些个
     * */
    @Test
    public void testClearUpperLimit() throws Exception {
        testContext.getConfig().setUpperKeyLimitOfPeriodicalDelete(10);
        for (int i = 0; i < 15; i ++) {
            testContext.getStorage().set("Hello" + i, "World");
            testContext.getExpireMap().backdoor().put("Hello" + i, 1636613116992L);
        }

        Proto.Response resp = callHandler(testContext);
        TestUtils.assertOK(Proto.MessageType.InnerClearExpire, resp);
        assertEquals(10, resp.getInnerClearExpireResponse().getCleared());
        assertEquals(5, testContext.getStorage().backdoor().size());
        assertEquals(5, testContext.getExpireMap().backdoor().size());
    }

    public static Proto.Response callHandler(GlobalContext testContext) {
        Proto.Request request =  Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerClearExpire)
                .setInnerClearExpireRequest(Proto.InnerClearExpireRequest.newBuilder())
                .build();
        Handler h = testContext.getHandlerFactory().getHandler(Proto.MessageType.InnerClearExpire).get();
        return h.handle(request);
    }
}