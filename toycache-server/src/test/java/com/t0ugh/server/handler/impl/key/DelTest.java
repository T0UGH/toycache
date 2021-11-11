package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.ValueObject;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
        testContext.getStorage().backdoor().put("Hello", ValueObject.newInstance("World"));
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试删除一个存在的键
     * */
    @Test
    public void testDel() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Del)
                .setDelRequest(Proto.DelRequest.newBuilder()
                        .setKey("Hello"))
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.Del).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Del, response);
        assertTrue(response.getDelResponse().getOk());
        assertNull(testContext.getStorage().get("Hello"));
    }

    /**
     * 测试删除一个不存在的键
     * */
    @Test
    public void testDelNotExists() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Del)
                .setDelRequest(Proto.DelRequest.newBuilder()
                        .setKey("Hi"))
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.Del).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Del, response);
        assertFalse(response.getDelResponse().getOk());
        assertNull(testContext.getStorage().get("Hi"));
    }

    /**
     * 测试删除一个过期的键, 确实会删, 但是会返回false
     * */
    @Test
    public void testDelExpired() throws Exception {
        testContext.getExpireMap().backdoor().put("Hello", 1636613116992L);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Del)
                .setDelRequest(Proto.DelRequest.newBuilder()
                        .setKey("Hello"))
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.Del).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Del, response);
        assertFalse(response.getDelResponse().getOk());
        assertNull(testContext.getStorage().get("Hello"));
    }
}