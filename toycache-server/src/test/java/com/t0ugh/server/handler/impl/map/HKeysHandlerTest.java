package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class HKeysHandlerTest extends BaseMapHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void test() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HKeys)
                .setHKeysRequest(Proto.HKeysRequest.newBuilder()
                        .setKey("Hi")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HKeys).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HKeys, response);
        List<String> fields = response.getHKeysResponse().getFieldsList();
        assertEquals(2, fields.size());
        assertTrue(fields.contains("Hello"));
        assertTrue(fields.contains("World"));
    }

    @Test
    public void test1() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HKeys)
                .setHKeysRequest(Proto.HKeysRequest.newBuilder()
                        .setKey("Ha")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HKeys).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HKeys, response);
        List<String> fields = response.getHKeysResponse().getFieldsList();
        assertEquals(0, fields.size());
    }
}