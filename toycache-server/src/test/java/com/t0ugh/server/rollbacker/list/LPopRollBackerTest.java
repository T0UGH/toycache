package com.t0ugh.server.rollbacker.list;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class LPopRollBackerTest extends BaseListRollBackerTest{

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
        List<String> origin = Lists.newArrayList(testContext.getStorage().backdoor().get("test").getListValue());
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LPop)
                .setLPopRequest(Proto.LPopRequest.newBuilder()
                        .setKey("test")
                        .build())
                .build();
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(Proto.MessageType.LPop).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.LPop).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.LPop, response);
        rollBacker.rollBack();
        TestUtils.assertCollectionEquals(origin, Lists.newArrayList(testContext.getStorage().backdoor().get("test").getListValue()));
    }
}
