package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.impl.set.BaseSetHandlerTest;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZCardHandlerTest extends BaseSortedSetHandlerTest {

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
        test("Hx", 0);
    }

    public void test(String key, int expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZCard)
                .setZCardRequest(Proto.ZCardRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.ZCard).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.ZCard, response);
        assertEquals(expected, response.getZCardResponse().getCount());
    }
}