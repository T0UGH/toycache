package com.t0ugh.server.handler.impl.string;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerTestBase;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.storage.ValueObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetHandlerTest extends HandlerTestBase {

    @Before
    public void setUp() throws Exception {
        testContext.getStorage().backdoor().put("Hello", ValueObject.newInstance("World"));
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * get一个Key, 看看返回的value对不对
     * */
    @Test
    public void testGet() {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Get)
                .setGetRequest(Proto.GetRequest.newBuilder()
                        .setKey("Hello"))
                .build();

        Handler handler = new GetHandler(testContext);
        Proto.Response response = handler.handle(request);
        assertEquals(Proto.ResponseCode.OK, response.getResponseCode());
        assertTrue(response.hasGetResponse());
        assertEquals("World", response.getGetResponse().getValue());
    }

    /**
     * get一个不存在的键
     * */
    @Test
    public void testGetNotExist() {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Get)
                .setGetRequest(Proto.GetRequest.newBuilder()
                        .setKey("Hi"))
                .build();
        Handler handler = new GetHandler(testContext);
        Proto.Response response = handler.handle(request);
        assertEquals(Proto.ResponseCode.OK, response.getResponseCode());
        assertTrue(response.hasGetResponse());
        assertTrue(Strings.isNullOrEmpty(response.getGetResponse().getValue()));
    }

    /**
     * get一个过期的键, 看看是否能够返回 ResponseCode.KeyExpired 并且storage中已经不存在这个key了(惰性删除掉了)
     * */
    @Test
    public void testGetExpired() {
        testContext.getExpireMap().backdoor().put("Hello", System.currentTimeMillis() - 1000);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Get)
                .setGetRequest(Proto.GetRequest.newBuilder()
                        .setKey("Hello"))
                .build();
        Handler handler = new GetHandler(testContext);
        Proto.Response response = handler.handle(request);
        assertEquals(Proto.ResponseCode.KeyExpired, response.getResponseCode());
        Storage storage = testContext.getStorage();
        assertFalse(storage.backdoor().containsKey("Hello"));
    }
}