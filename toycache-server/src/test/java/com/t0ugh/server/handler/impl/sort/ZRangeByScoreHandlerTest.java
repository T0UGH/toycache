package com.t0ugh.server.handler.impl.sort;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ZRangeByScoreHandlerTest extends BaseSortedSetHandlerTest {

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
                DBProto.ComparableString.newBuilder().setStringValue("H2").setScore(3).build()
        );
        test("Hi", 3, 5, expected);
    }

    @Test
    public void test2() throws Exception {
        List<DBProto.ComparableString> expected = Lists.newArrayList(
                DBProto.ComparableString.newBuilder().setStringValue("H1").setScore(5).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H2").setScore(3).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H3").setScore(1).build()
        );
        test("Hi", 0, 7, expected);
    }

    @Test
    public void test3() throws Exception {
        test("Hi", 100, 1000, Lists.newArrayList());
    }

    @Test
    public void test4() throws Exception {
        test("Ha", 0, 7, Lists.newArrayList());
    }

    public void test(String key, double min, double max, List<DBProto.ComparableString> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRangeByScore)
                .setZRangeByScoreRequest(Proto.ZRangeByScoreRequest.newBuilder()
                        .setKey(key)
                        .setMin(min)
                        .setMax(max)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.ZRangeByScore).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.ZRangeByScore, response);
        assertEquals(expected.size(), response.getZRangeByScoreResponse().getValuesList().size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getStringValue(), response.getZRangeByScoreResponse().getValuesList().get(i).getStringValue());
            assertEquals(expected.get(i).getScore(), response.getZRangeByScoreResponse().getValuesList().get(i).getScore(), 0.0);
        }
    }
}