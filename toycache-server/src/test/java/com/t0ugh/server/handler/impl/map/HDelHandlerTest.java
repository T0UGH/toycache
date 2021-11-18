package com.t0ugh.server.handler.impl.map;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HDelHandlerTest extends BaseMapHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试删除
     * */
    @Test
    public void test() throws Exception {
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HDel).get();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HDel)
                .setHDelRequest(Proto.HDelRequest.newBuilder()
                        .setKey("Hi")
                        .addAllFields(Lists.newArrayList("Hello", "World", "Js"))
                        .build())
                .build();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HDel, response);
        assertEquals(2, response.getHDelResponse().getDeleted());
        assertEquals(0, testContext.getStorage().backdoor().get("Hi").getMapValue().size());

    }
}