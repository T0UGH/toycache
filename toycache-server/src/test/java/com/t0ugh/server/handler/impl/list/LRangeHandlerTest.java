package com.t0ugh.server.handler.impl.list;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LRangeHandlerTest extends BaseListHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", 0, 2, Lists.newArrayList("H1", "H2", "H3"));
    }

    @Test
    public void test2() throws Exception {
        test("Hi", 0, -1, Lists.newArrayList("H1", "H2", "H3"));
    }

    @Test
    public void test3() throws Exception {
        test("Hi", 0, 1, Lists.newArrayList("H1", "H2"));
    }

    @Test
    public void test4() throws Exception {
        test("Hi", 0, 100, Lists.newArrayList("H1", "H2", "H3"));
    }

    @Test
    public void test5() throws Exception {
        test("Hi", -2, -1, Lists.newArrayList("H2", "H3"));
    }

    @Test
    public void test6() throws Exception {
        test("Hi", -1, -1, Lists.newArrayList("H3"));
    }

    @Test
    public void test7() throws Exception {
        test("Ha", -1, -1, Lists.newArrayList());
    }

    public void test(String key, int start, int end, List<String> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LRange)
                .setLRangeRequest(Proto.LRangeRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LRange).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LRange, response);
        assertEquals(expected.size(), response.getLRangeResponse().getValuesList().size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), response.getLRangeResponse().getValuesList().get(i));
        }
    }
}