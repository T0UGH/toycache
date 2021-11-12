package com.t0ugh.server.handler.impl.string;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SetTest extends BaseTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试能不能为key设置指定的value
     * */
    @Test
    public void testHandle() throws Exception {
        assertFalse(testContext.getStorage().backdoor().containsKey("Hello"));
        SetHandler setHandler = new SetHandler(testContext);
        Storage storage = testContext.getStorage();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Hello")
                        .setValue("World")).build();
        Proto.Response response = setHandler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Set, response);
        DBProto.ValueObject vo = storage.backdoor().get("Hello");
        assertNotNull(vo.getStringValue());
        assertEquals(vo.getValueType(), DBProto.ValueType.ValueTypeString);
        assertEquals(vo.getStringValue(), "World");
    }

    /**
     * 连续为同一个key设置两次value
     * */
    @Test
    public void testSameKey() {
        assertFalse(testContext.getStorage().backdoor().containsKey("Hello"));
        SetHandler setHandler = new SetHandler(testContext);
        Storage storage = testContext.getStorage();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Hello")
                        .setValue("World")).build();
        Proto.Response response = setHandler.handle(request);
        Proto.Request request2 = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Hello")
                        .setValue("War")).build();
        Proto.Response response2 = setHandler.handle(request2);
        DBProto.ValueObject vo = storage.backdoor().get("Hello");
        assertNotNull(vo.getStringValue());
        assertEquals(DBProto.ValueType.ValueTypeString, vo.getValueType());
        assertEquals("War", vo.getStringValue());
    }
}