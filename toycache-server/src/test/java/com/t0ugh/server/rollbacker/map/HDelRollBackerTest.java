package com.t0ugh.server.rollbacker.map;

import com.google.common.collect.Maps;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class HDelRollBackerTest extends BaseMapRollBackerTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试提交一条HDelRequest，然后回滚
     * */
    @Test
    public void test() throws Exception {
        Map<String, String> origin = Maps.newHashMap(testContext.getStorage().backdoor().get("test").getMapValue());
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HDel)
                .setHDelRequest(Proto.HDelRequest.newBuilder()
                        .setKey("test")
                        .addFields("Hello")
                        .addFields("Hi")
                        .build())
                .build();
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(Proto.MessageType.HDel).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HDel).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HDel, response);
        rollBacker.rollBack();
        TestUtils.assertMapEquals(origin, Maps.newHashMap(testContext.getStorage().backdoor().get("test").getMapValue()));
    }

}
