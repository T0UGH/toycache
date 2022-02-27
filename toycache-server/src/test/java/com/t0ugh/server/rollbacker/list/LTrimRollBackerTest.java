package com.t0ugh.server.rollbacker.list;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LTrimRollBackerTest extends BaseListRollBackerTest{

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
                .setMessageType(Proto.MessageType.LTrim)
                .setLTrimRequest(Proto.LTrimRequest.newBuilder()
                        .setKey("test")
                        .setStart(0)
                        .setEnd(0)
                        .build())
                .build();
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(Proto.MessageType.LTrim).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.LTrim).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.LTrim, response);
        rollBacker.rollBack();
        TestUtils.assertCollectionEquals(origin, Lists.newArrayList(testContext.getStorage().backdoor().get("test").getListValue()));
    }
}
