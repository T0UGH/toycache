package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SRandMemberHandlerTest extends BaseSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", 3, 3);
    }


    @Test
    public void test2() throws Exception {
        test("Hi", 4, 3);
    }

    @Test
    public void test3() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SRandMember)
                .setSRandMemberRequest(Proto.SRandMemberRequest.newBuilder()
                        .setKey("Hx")
                        .setCount(1)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SRandMember).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SRandMember, response);
        assertEquals(0, response.getSRandMemberResponse().getSetValueList().size());
    }

    public void test(String key, int count, int expectedCount) throws Exception {
        Storage storage = testContext.getStorage();
        Set<String> origin = Sets.newHashSet(storage.backdoor().get(key).getSetValue());
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SRandMember)
                .setSRandMemberRequest(Proto.SRandMemberRequest.newBuilder()
                        .setKey(key)
                        .setCount(count)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SRandMember).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SRandMember, response);
        Set<String> now = storage.backdoor().get(key).getSetValue();
        assertEquals(origin.size(), now.size());
        assertTrue(origin.containsAll(now));
        assertEquals(expectedCount, response.getSRandMemberResponse().getSetValueList().size());
        assertTrue(now.containsAll(response.getSRandMemberResponse().getSetValueList()));
    }
}