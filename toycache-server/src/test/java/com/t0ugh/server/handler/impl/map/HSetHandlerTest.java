package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HSetHandlerTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试往一个key中连续set两个field-value对
     * */
    @Test
    public void test1() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HSet)
                .setHSetRequest(Proto.HSetRequest.newBuilder()
                        .setKey("hello")
                        .setField("hi")
                        .setValue("world")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HSet).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HSet, response);
        assertTrue(response.getHSetResponse().getOk());
        Storage storage = testContext.getStorage();
        assertTrue(storage.backdoor().containsKey("hello"));
        assertEquals(DBProto.ValueType.ValueTypeMap, storage.backdoor().get("hello").getValueType());
        assertEquals("world", storage.backdoor().get("hello").getMapValue().get("hi"));
        Proto.Request request2 = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HSet)
                .setHSetRequest(Proto.HSetRequest.newBuilder()
                        .setKey("hello")
                        .setField("haha")
                        .setValue("world")
                        .build())
                .build();
        Proto.Response response2 = handler.handle(request2);
        TestUtils.assertOK(Proto.MessageType.HSet, response2);
        assertTrue(response2.getHSetResponse().getOk());
        assertEquals("world", storage.backdoor().get("hello").getMapValue().get("haha"));
    }
}