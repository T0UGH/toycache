package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.GlobalState;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.utils.MessageUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InnerRewriteLogFinishHandlerTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试当{@link GlobalState#getRewriteLogState()}为{@link com.t0ugh.server.enums.RewriteLogState#RewritingKeys}时收到一条InnerRewriteFinish请求
     * 1. 能够更新状态为{@link com.t0ugh.server.enums.RewriteLogState#RewritingKeys}
     * */
    @Test
    public void test1() throws Exception {
        testContext.getGlobalState().setRewriteLogState(RewriteLogState.RewritingKeys);
        Proto.Request request = MessageUtils.newInnerRewriteLogFinishRequest(true);
        testContext.getHandlerFactory().getHandler(Proto.MessageType.InnerRewriteLogFinish).get().handle(request);
        assertEquals(RewriteLogState.Normal, testContext.getGlobalState().getRewriteLogState());
    }
}