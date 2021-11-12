package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.sdk.proto.ValueObjects;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExistsTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
        testContext.getStorage().backdoor().put("Hello", ValueObjects.newInstance("World"));
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试一个存在的键是否能返回true
     * */
    @Test
    public void testExist() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Exists)
                .setExistsRequest(Proto.ExistsRequest.newBuilder()
                        .setKey("Hello"))
                .build();
        Handler handler = new ExistsHandler(testContext);
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Exists, response);
        assertTrue(response.hasExistsResponse());
        assertTrue(response.getExistsResponse().getExists());
    }

    @Test
    public void testNotExist() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Exists)
                .setExistsRequest(Proto.ExistsRequest.newBuilder()
                        .setKey("Hi"))
                .build();
        Handler handler = new ExistsHandler(testContext);
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Exists, response);
        assertTrue(response.hasExistsResponse());
        assertFalse(response.getExistsResponse().getExists());
    }

    /**
     * 测试一个已过期的key,过期了也返回false,并且会惰性删除
     * */
    @Test
    public void testExistExpire() throws Exception {
        testContext.getExpireMap().backdoor().put("Hello", 1636613116992L);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Exists)
                .setExistsRequest(Proto.ExistsRequest.newBuilder()
                        .setKey("Hello"))
                .build();
        Handler handler = new ExistsHandler(testContext);
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Exists, response);
        assertFalse(response.getExistsResponse().getExists());
        assertFalse(testContext.getStorage().backdoor().containsKey("Hello"));
        assertFalse(testContext.getExpireMap().backdoor().containsKey("Hello"));
    }
}