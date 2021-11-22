package com.t0ugh.server.handler.impl.sort;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZCountHandlerTest extends BaseSortedSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", 3, 5, 2);
    }

    @Test
    public void test2() throws Exception {
        test("Hi", 0, 100, 3);
    }

    @Test
    public void test3() throws Exception {
        test("Ha", 3, 5, 0);
    }

    public void test(String key, double min, double max, int expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZCount)
                .setZCountRequest(Proto.ZCountRequest.newBuilder()
                        .setKey(key)
                        .setMin(min)
                        .setMax(max)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.ZCount).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.ZCount, response);
        assertEquals(expected, response.getZCountResponse().getCount());
    }
}