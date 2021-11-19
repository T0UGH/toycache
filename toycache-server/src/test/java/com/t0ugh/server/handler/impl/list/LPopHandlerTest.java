package com.t0ugh.server.handler.impl.list;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LPopHandlerTest extends BaseListHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception{
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPop)
                .setLPopRequest(Proto.LPopRequest.newBuilder()
                        .setKey("Hi")
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LPop).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LPop, response);
        assertEquals("H1", response.getLPopResponse().getValue());
    }

    @Test
    public void test1() throws Exception{
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPop)
                .setLPopRequest(Proto.LPopRequest.newBuilder()
                        .setKey("Hx")
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LPop).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LPop, response);
        assertTrue(Strings.isNullOrEmpty(response.getLPopResponse().getValue()));
    }
}