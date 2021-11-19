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

public class SPopHandlerTest extends BaseSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", 1, 1);
    }

    @Test
    public void test2() throws Exception {
        test("Hi", 4, 3);
    }

    @Test
    public void test3() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SPop)
                .setSPopRequest(Proto.SPopRequest.newBuilder()
                        .setKey("Hx")
                        .setCount(4)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SPop).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SPop, response);
        assertEquals(0, response.getSPopResponse().getSetValueList().size());
    }

    public void test(String key, int count, int expectedCount) throws Exception {
        Storage storage = testContext.getStorage();
        int total = storage.backdoor().get(key).getSetValue().size();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SPop)
                .setSPopRequest(Proto.SPopRequest.newBuilder()
                        .setKey(key)
                        .setCount(count)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SPop).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SPop, response);
        assertEquals(expectedCount, response.getSPopResponse().getSetValueList().size());
        assertEquals(total, expectedCount + storage.backdoor().get(key).getSetValue().size());
        assertEquals(0, Sets.intersection(
                storage.backdoor().get(key).getSetValue(),
                Sets.newHashSet(response.getSPopResponse().getSetValueList())).size());
    }
}