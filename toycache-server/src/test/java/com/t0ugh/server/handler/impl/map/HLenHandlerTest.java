package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HLenHandlerTest extends BaseMapHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试一个有两个field的key的长度
     * */
    @Test
    public void test() throws Exception {
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HLen).get();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HLen)
                .setHLenRequest(Proto.HLenRequest.newBuilder()
                        .setKey("Hi")
                        .build())
                .build();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HLen, response);
        assertEquals(2, response.getHLenResponse().getLen());
    }

    /**
     * 测试一个不存在key的长度
     * */
    @Test
    public void test2() throws Exception {
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HLen).get();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HLen)
                .setHLenRequest(Proto.HLenRequest.newBuilder()
                        .setKey("Hz")
                        .build())
                .build();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HLen, response);
        assertEquals(0, response.getHLenResponse().getLen());
    }
}