package com.t0ugh.server.rollbacker.sort;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZRemRollBackerTest extends BaseSortedSetRollBackerTest{

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
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRem)
                .setZRemRequest(Proto.ZRemRequest.newBuilder()
                        .setKey("Hi")
                        .addMembers("H1")
                        .addMembers("H0")
                        .build())
                .build();
        TestUtils.testRollBackerCollectionUnchanged(request, "Hi", testContext);
    }
}
