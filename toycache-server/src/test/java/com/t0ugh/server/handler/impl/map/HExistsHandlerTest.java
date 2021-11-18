package com.t0ugh.server.handler.impl.map;

import com.google.common.collect.Maps;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class HExistsHandlerTest extends BaseMapHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试一个存在的key和存在的field，ok=true
     * */
    @Test
    public void test() throws Exception {
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HExists).get();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HExists)
                .setHExistsRequest(Proto.HExistsRequest.newBuilder()
                        .setKey("Hi")
                        .setField("Hello")
                )
                .build();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HExists, response);
        assertTrue(response.getHExistsResponse().getOk());
    }

    /**
     * 测试一个不存在的key，ok=false
     * */
    @Test
    public void test2() throws Exception {
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HExists).get();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HExists)
                .setHExistsRequest(Proto.HExistsRequest.newBuilder()
                        .setKey("Ha")
                        .setField("Hello")
                )
                .build();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HExists, response);
        assertFalse(response.getHExistsResponse().getOk());
    }

    /**
     * 测试一个存在的key但不存在的field，ok=false
     * */
    @Test
    public void test3() throws Exception {
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HExists).get();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HExists)
                .setHExistsRequest(Proto.HExistsRequest.newBuilder()
                        .setKey("Hi")
                        .setField("Ha")
                )
                .build();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HExists, response);
        assertFalse(response.getHExistsResponse().getOk());
    }
}