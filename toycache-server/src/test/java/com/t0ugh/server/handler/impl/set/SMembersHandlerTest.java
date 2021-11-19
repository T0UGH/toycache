package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SMembersHandlerTest extends BaseSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", Sets.newHashSet("H1", "H2", "H3"));
    }

    @Test
    public void test2() throws Exception {
        test("Hx", Sets.newHashSet());
    }

    public void test(String key, Set<String> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SMembers)
                .setSMembersRequest(Proto.SMembersRequest.newBuilder()
                        .setKey(key)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SMembers).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SMembers, response);
        assertEquals(expected.size(), response.getSMembersResponse().getSetValueList().size());
        assertTrue(expected.containsAll(response.getSMembersResponse().getSetValueList()));
    }
}