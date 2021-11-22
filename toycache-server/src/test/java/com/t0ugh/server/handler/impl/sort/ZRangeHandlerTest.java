package com.t0ugh.server.handler.impl.sort;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ZRangeHandlerTest extends BaseSortedSetHandlerTest{
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        List<DBProto.ComparableString> expected = Lists.newArrayList(
                DBProto.ComparableString.newBuilder().setStringValue("H1").setScore(5).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H2").setScore(3).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H3").setScore(1).build());
        test("Hi", 0, -1, expected);
    }

    @Test
    public void test2() throws Exception {
        List<DBProto.ComparableString> expected = Lists.newArrayList(
                DBProto.ComparableString.newBuilder().setStringValue("H2").setScore(3).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H3").setScore(1).build());
        test("Hi", -2, -1, expected);
    }

    @Test
    public void test3() throws Exception {
        List<DBProto.ComparableString> expected = Lists.newArrayList(
                DBProto.ComparableString.newBuilder().setStringValue("H2").setScore(3).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H3").setScore(1).build());
        test("Hi", 1, 100, expected);
    }

    @Test
    public void test4() throws Exception {
        List<DBProto.ComparableString> expected = Lists.newArrayList();
        test("Hx", 0, 100, expected);
    }

    public void test(String key, int start, int end, List<DBProto.ComparableString> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRange)
                .setZRangeRequest(Proto.ZRangeRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.ZRange).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.ZRange, response);
        assertEquals(expected.size(), response.getZRangeResponse().getValuesList().size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getStringValue(), response.getZRangeResponse().getValuesList().get(i).getStringValue());
            assertEquals(expected.get(i).getScore(), response.getZRangeResponse().getValuesList().get(i).getScore(), 0.0);
        }
    }
}
