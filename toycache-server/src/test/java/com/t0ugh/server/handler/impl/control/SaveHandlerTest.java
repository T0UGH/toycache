package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.tick.MessageExecutorTestImpl;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaveHandlerTest extends BaseTest {

    MessageExecutorTestImpl mockDbExecutor;

    @Before
    public void setUp() throws Exception {
//        testContext.setDbExecutor();
        testContext.getStorage().set("Hello", "World");
        testContext.getStorage().set("Hi", "World");
        mockDbExecutor = new MessageExecutorTestImpl();
        testContext.setDbExecutor(mockDbExecutor);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试当Handler内部状态为Idle时收到了Save请求
     * 1. 能够往dbExecutor发条消息
     * 2. 能够response一个OK
     * */
    @Test
    public void test1() throws Exception {
        SaveHandler handler = (SaveHandler) testContext.getHandlerFactory().getHandler(Proto.MessageType.Save).get();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Save)
                .setSaveRequest(Proto.SaveRequest.newBuilder())
                .build();
        Proto.Response response = handler.handle(request);
        Thread.sleep(100);
        TestUtils.assertOK(Proto.MessageType.Save, response);
        assertTrue(response.getSaveResponse().getOk());
        assertEquals(1, mockDbExecutor.requestList.size());
        Proto.Request request1 = mockDbExecutor.requestList.get(0);
        assertEquals(Proto.MessageType.InnerSave, request1.getMessageType());
        assertTrue(request1.hasInnerSaveRequest());
        assertEquals(2, request1.getInnerSaveRequest().getDb().getDataCount());

    }

    /**
     * 测试当Handler内部状态为Running时收到了Save请求
     * 1. 不往dbExecutor发消息
     * 2. 能够response一个OK=false
     * */
    @Test
    public void test2() throws Exception {
        SaveHandler handler = (SaveHandler) testContext.getHandlerFactory().getHandler(Proto.MessageType.Save).get();
        testContext.getGlobalState().setSaveState(SaveState.Running);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Save)
                .setSaveRequest(Proto.SaveRequest.newBuilder())
                .build();
        Proto.Response response = handler.handle(request);
        Thread.sleep(100);
        TestUtils.assertOK(Proto.MessageType.Save, response);
        assertFalse(response.getSaveResponse().getOk());
        assertEquals(0, mockDbExecutor.requestList.size());
    }

    /**
     * 测试当Handler内部状态为Running时收到了InnerSaveFinishRequest请求
     * 1. 将状态置为Idle
     * 2. 返回OK
     * */
    @Test
    public void test3() throws Exception {
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.InnerSaveFinish).get();
        testContext.getGlobalState().setSaveState(SaveState.Running);
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerSaveFinish)
                .setInnerSaveFinishRequest(Proto.InnerSaveFinishRequest.newBuilder().setOk(true))
                .build();
        Proto.Response response = handler.handle(request);
        Thread.sleep(100);
        TestUtils.assertOK(Proto.MessageType.InnerSaveFinish, response);
        assertTrue(response.getInnerSaveFinishResponse().getOk());
        assertEquals(0, mockDbExecutor.requestList.size());
        assertEquals(SaveState.Idle, testContext.getGlobalState().getSaveState());
    }
}