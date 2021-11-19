package com.t0ugh.server.handler.impl.list;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LIndexHandlerTest extends BaseListHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LIndex)
                .setLIndexRequest(Proto.LIndexRequest.newBuilder()
                        .setKey("Hi")
                        .setIndex(0))
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LIndex).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LIndex, response);
        assertEquals("H1", response.getLIndexResponse().getValue());
    }

    @Test
    public void test1() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LIndex)
                .setLIndexRequest(Proto.LIndexRequest.newBuilder()
                        .setKey("Hi")
                        .setIndex(3))
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LIndex).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LIndex, response);
        assertTrue(Strings.isNullOrEmpty(response.getLIndexResponse().getValue()));
    }

    @Test
    public void test2() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LIndex)
                .setLIndexRequest(Proto.LIndexRequest.newBuilder()
                        .setKey("Ha")
                        .setIndex(0))
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LIndex).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LIndex, response);
        assertTrue(Strings.isNullOrEmpty(response.getLIndexResponse().getValue()));
    }

}