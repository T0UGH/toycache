package com.t0ugh.server.handler.impl.map;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HGetHandlerTest extends BaseMapHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HGet)
                .setHGetRequest(Proto.HGetRequest.newBuilder()
                        .setKey("Hi")
                        .setField("Hello")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HGet).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HGet, response);
        assertEquals("World",response.getHGetResponse().getValue());
    }

    @Test
    public void test1() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HGet)
                .setHGetRequest(Proto.HGetRequest.newBuilder()
                        .setKey("Hix")
                        .setField("Hello")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HGet).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HGet, response);
        assertTrue(Strings.isNullOrEmpty(response.getHGetResponse().getValue()));
    }

    @Test
    public void test2() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HGet)
                .setHGetRequest(Proto.HGetRequest.newBuilder()
                        .setKey("Hi")
                        .setField("Hellox")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HGet).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HGet, response);
        assertTrue(Strings.isNullOrEmpty(response.getHGetResponse().getValue()));
    }
}