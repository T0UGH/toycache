package com.t0ugh.server.handler.impl.key;

import com.google.common.primitives.Longs;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpireTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
        testContext.getStorage().backdoor().put("Hello", MemoryValueObject.newInstance("World"));
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
                        .setExpireTime(System.currentTimeMillis()+1000L))
                .build();
        Handler expireHandler = new ExpireHandler(testContext);
        Proto.Response resp = expireHandler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Expire, resp);
        assertTrue(resp.getExpireResponse().getOk());
        Storage storage = testContext.getStorage();
        assertTrue(Longs.compare(storage.getExpireMap().get("Hello"), 0L) > 0);
        System.out.println(storage.getExpireMap().get("Hello"));
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
                        .setExpireTime(System.currentTimeMillis()+1000L))
                .build();
        Handler expireHandler = new ExpireHandler(testContext);
        Proto.Response resp = expireHandler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Expire, resp);
        assertFalse(resp.getExpireResponse().getOk());
        assertNull(testContext.getStorage().getExpireMap().get("Hi"));
    }

    /**
     * 测试为一个过期的的key设置ExpireTime
     * */
    @Test
    public void testExpireExpired() throws Exception {
        testContext.getStorage().getExpireMap().put("Hello", 1636613116992L);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Expire)
                .setExpireRequest(Proto.ExpireRequest.newBuilder()
                        .setKey("Hello")
                        .setExpireTime(System.currentTimeMillis()+1000L))
                .build();
        Handler expireHandler = new ExpireHandler(testContext);
        Proto.Response resp = expireHandler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Expire, resp);
        assertNull(testContext.getStorage().getExpireMap().get("Hello"));
    }
}