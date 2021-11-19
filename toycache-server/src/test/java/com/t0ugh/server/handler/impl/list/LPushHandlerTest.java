package com.t0ugh.server.handler.impl.list;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LPushHandlerTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPush)
                .setLPushRequest(Proto.LPushRequest.newBuilder()
                        .setKey("Hi")
                        .addAllValue(Lists.newArrayList("Hi1", "Hi2"))
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.LPush).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.LPush, response);
        assertEquals(2, response.getLPushResponse().getSize());
        Storage storage = testContext.getStorage();
        assertEquals(2, storage.backdoor().get("Hi").getListValue().size());
        assertEquals("Hi1", storage.backdoor().get("Hi").getListValue().get(0));
        assertEquals("Hi2", storage.backdoor().get("Hi").getListValue().get(1));
        Proto.Request request1 = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPush)
                .setLPushRequest(Proto.LPushRequest.newBuilder()
                        .setKey("Hi")
                        .addAllValue(Lists.newArrayList("Hi3"))
                        .build())
                .build();
        Proto.Response response1 = handler.handle(request1);
        TestUtils.assertOK(Proto.MessageType.LPush, response1);
        assertEquals(3, response1.getLPushResponse().getSize());
        assertEquals(3, storage.backdoor().get("Hi").getListValue().size());
        assertEquals("Hi3", storage.backdoor().get("Hi").getListValue().get(0));
        assertEquals("Hi1", storage.backdoor().get("Hi").getListValue().get(1));
        assertEquals("Hi2", storage.backdoor().get("Hi").getListValue().get(2));
    }
}