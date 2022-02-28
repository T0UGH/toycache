package com.t0ugh.server.rollbacker.set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class SAddRollBackerTest extends BaseSetRollBackerTest{
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试提交一条SAddRequest，然后回滚
     * */
    @Test
    public void test() throws Exception {
        Set<String> origin = Sets.newHashSet(testContext.getStorage().backdoor().get("test").getSetValue());
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SAdd)
                .setSAddRequest(Proto.SAddRequest.newBuilder()
                        .setKey("test")
                        .addSetValue("Hello")
                        .addSetValue("Hi")
                        .build())
                .build();
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(Proto.MessageType.SAdd).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.SAdd).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.SAdd, response);
        rollBacker.rollBack();
        TestUtils.assertCollectionEquals(origin, Sets.newHashSet(testContext.getStorage().backdoor().get("test").getSetValue()));
    }
}
