package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SIsMemberHandlerTest extends BaseSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", "H1", true);
    }


    @Test
    public void test2() throws Exception {
        test("Hi", "H9", false);
    }


    @Test
    public void test3() throws Exception {
        test("Hx", "H1", false);
    }


    public void test(String key, String member, boolean expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SIsMember)
                .setSIsMemberRequest(Proto.SIsMemberRequest.newBuilder()
                        .setKey(key)
                        .setMember(member)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SIsMember).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SIsMember, response);
        assertEquals(expected, response.getSIsMemberResponse().getIsMember());
    }
}