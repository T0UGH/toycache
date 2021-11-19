package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LLenHandlerTest extends BaseListHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", 3);
    }

    @Test
    public void test2() throws Exception {
        test("Ha", 0);
    }

    public void test(String key, int count) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LLen)
                .setLLenRequest(Proto.LLenRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LLen).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LLen, response);
        assertEquals(count, response.getLLenResponse().getCount());
    }
}