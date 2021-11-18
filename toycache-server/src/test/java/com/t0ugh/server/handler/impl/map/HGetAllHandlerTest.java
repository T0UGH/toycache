package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class HGetAllHandlerTest extends BaseMapHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HGetAll)
                .setHGetAllRequest(Proto.HGetAllRequest.newBuilder()
                        .setKey("Hi")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HGetAll).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HGetAll, response);
        Map<String, String> kvs = response.getHGetAllResponse().getKvsMap();
        assertEquals(2, kvs.size());
        assertEquals("World", kvs.get("Hello"));
        assertEquals("World", kvs.get("World"));
    }

    @Test
    public void test2() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HGetAll)
                .setHGetAllRequest(Proto.HGetAllRequest.newBuilder()
                        .setKey("Hix")
                        .build())
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HGetAll).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HGetAll, response);
        Map<String, String> kvs = response.getHGetAllResponse().getKvsMap();
        assertEquals(0, kvs.size());
    }
}