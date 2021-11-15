package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.GlobalState;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.tick.MessageExecutorTestImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RewriteLogHandlerTest extends BaseTest {

    MessageExecutorTestImpl messageExecutorTest;

    @Before
    public void setUp() throws Exception {
        messageExecutorTest = new MessageExecutorTestImpl();
        testContext.setWriteLogExecutor(messageExecutorTest);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试当{@link GlobalState#getRewriteLogState()}为{@link com.t0ugh.server.enums.RewriteLogState#Normal}时收到一条Rewrite请求
     * 1. 能够向WriteLogExecutor提交一条InnerSaveRequest请求，并且请求中携带了当前数据库状态
     * 2. 能够更新状态为{@link com.t0ugh.server.enums.RewriteLogState#Rewriting}
     * 3. Response中的OK为True
     * */
    @Test
    public void test1() throws Exception {
        testContext.getStorage().backdoor().put("Hello", MemoryValueObject.newInstance("World"));
        testContext.getStorage().expireBackdoor().put("Hello", System.currentTimeMillis() + 100000L);
        testContext.getGlobalState().setRewriteLogState(RewriteLogState.Normal);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.RewriteLog)
                .setRewriteLogRequest(Proto.RewriteLogRequest.newBuilder().build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.RewriteLog).get().handle(request);
        assertEquals(RewriteLogState.Rewriting, testContext.getGlobalState().getRewriteLogState());
        assertTrue(response.getRewriteLogResponse().getOk());
        assertEquals(1, messageExecutorTest.requestList.size());
        Proto.Request innerRequest = messageExecutorTest.requestList.get(0);
        assertEquals(Proto.MessageType.InnerRewriteLog, innerRequest.getMessageType());
        assertEquals(1, innerRequest.getInnerRewriteLogRequest().getDb().getDataCount());
        assertEquals("World", innerRequest.getInnerRewriteLogRequest().getDb().getDataMap().get("Hello").getStringValue());
        assertEquals(1, innerRequest.getInnerRewriteLogRequest().getDb().getExpireCount());
    }

    /**
     * 测试当{@link GlobalState#getRewriteLogState()}为{@link com.t0ugh.server.enums.RewriteLogState#Rewriting}时收到一条Rewrite请求
     * 1. 不提交请求
     * 2. 直接返回OK为false的Response
     * */
    @Test
    public void test2() throws Exception {
        testContext.getGlobalState().setRewriteLogState(RewriteLogState.Rewriting);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.RewriteLog)
                .setRewriteLogRequest(Proto.RewriteLogRequest.newBuilder().build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.RewriteLog).get().handle(request);
        assertEquals(0, messageExecutorTest.requestList.size());
        assertFalse(response.getRewriteLogResponse().getOk());
    }
}