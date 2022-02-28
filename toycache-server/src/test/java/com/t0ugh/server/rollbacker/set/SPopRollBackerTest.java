package com.t0ugh.server.rollbacker.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class SPopRollBackerTest extends BaseSetRollBackerTest{
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试提交一条SPopRequest，然后回滚
     * */
    @Test
    public void test() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SPop)
                .setSPopRequest(Proto.SPopRequest.newBuilder()
                        .setKey("test")
                        .setCount(1)
                        .build())
                .build();
        TestUtils.testRollBackerCollectionUnchanged(request, "test", testContext);
    }
}
