package com.t0ugh.server.handler.impl.key;

import com.google.common.primitives.Longs;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerTestBase;
import com.t0ugh.server.storage.ExpireMap;
import com.t0ugh.server.storage.ValueObject;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpireHandlerTest extends HandlerTestBase {

    @Before
    public void setUp() throws Exception {
        testContext.getStorage().backdoor().put("Hello", ValueObject.newInstance("World"));
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试为一个key设置超时时间
     * */
    @Test
    public void testExpire() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Expire)
                .setExpireRequest(Proto.ExpireRequest.newBuilder()
                        .setKey("Hello")
                        .setExpireTime(1000L))
                .build();
        Handler expireHandler = new ExpireHandler(testContext);
        Proto.Response resp = expireHandler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Expire, resp);
        assertTrue(resp.getExpireResponse().getOk());
        ExpireMap expireMap = testContext.getExpireMap();
        assertTrue(Longs.compare(expireMap.backdoor().get("Hello"), 0L) > 0);
        System.out.println(expireMap.backdoor().get("Hello"));
    }

    /**
     * 测试为一个并不存在的key设置ExpireTime
     * */
    @Test
    public void testExpireNotExists() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Expire)
                .setExpireRequest(Proto.ExpireRequest.newBuilder()
                        .setKey("Hi")
                        .setExpireTime(1000L))
                .build();
        Handler expireHandler = new ExpireHandler(testContext);
        Proto.Response resp = expireHandler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Expire, resp);
        assertFalse(resp.getExpireResponse().getOk());
        ExpireMap expireMap = testContext.getExpireMap();
        assertNull(expireMap.backdoor().get("Hi"));
    }

    /**
     * 测试为一个过期的的key设置ExpireTime
     * */
    @Test
    public void testExpireExpired() throws Exception {
        testContext.getExpireMap().backdoor().put("Hello", 1636613116992L);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Expire)
                .setExpireRequest(Proto.ExpireRequest.newBuilder()
                        .setKey("Hello")
                        .setExpireTime(1000L))
                .build();
        Handler expireHandler = new ExpireHandler(testContext);
        Proto.Response resp = expireHandler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Expire, resp);
        ExpireMap expireMap = testContext.getExpireMap();
        assertNull(expireMap.backdoor().get("Hello"));
    }
}