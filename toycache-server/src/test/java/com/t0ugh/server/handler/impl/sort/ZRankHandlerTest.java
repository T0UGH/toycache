package com.t0ugh.server.handler.impl.sort;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZRankHandlerTest extends BaseSortedSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", "H1", true, 0);
    }

    @Test
    public void test2() throws Exception {
        test("Hi", "H2", true, 1);
    }


    @Test
    public void test3() throws Exception {
        test("Hi", "H3", true, 2);
    }

    @Test
    public void test4() throws Exception {
        test("Hi", "H4", false, -1);
    }

    @Test
    public void test5() throws Exception {
        test("Hx", "H4", false, -1);
    }


    public void test(String key, String member, boolean exists, int rank) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRank)
                .setZRankRequest(Proto.ZRankRequest.newBuilder()
                        .setKey(key)
                        .setMember(member)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.ZRank).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.ZRank, response);
        assertEquals(exists, response.getZRankResponse().getExists());
        if(exists){
            assertEquals(rank, response.getZRankResponse().getRank());
        }
    }
}