package com.t0ugh.server.tick;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaveTickerTest extends BaseTest {

    SaveTicker saveTicker;
    TickDriverTestImpl tickDriver;
    MessageExecutorTestImpl messageExecutorForTest;

    @Before
    public void setUp() throws Exception {
        messageExecutorForTest = new MessageExecutorTestImpl();
        testContext.setMemoryOperationExecutor(messageExecutorForTest);
        tickDriver = new TickDriverTestImpl(testContext);
        saveTicker = new SaveTicker(testContext);
        tickDriver.register(saveTicker);
    }

    /**
     * 当tick到足够的次数且更新次数达标时能够发出命令
     * */
    @Test
    public void testSendRequest() throws Exception {
        testContext.getGlobalState().getWriteCount().set(testContext.getConfig().getSaveCheckLimit());
        for(int i = 0; i < testContext.getConfig().getSaveCheckTick(); i ++){
            tickDriver.tickManually();
        }
        Thread.sleep(200);
        assertEquals(1, messageExecutorForTest.requestList.size());
        Proto.Request request = messageExecutorForTest.requestList.get(0);
        assertEquals(Proto.MessageType.Save, request.getMessageType());
        assertTrue(request.hasSaveRequest());
    }

    /**
     * 当tick到足够的次数且更新次数达标时能够发出命令, 然后再tick足够的次数然后检测更新次数是否达标，又达标又发出命令
     * */
    @Test
    public void testSendRequest3() throws Exception {
        testContext.getGlobalState().getWriteCount().set(testContext.getConfig().getSaveCheckLimit());
        for(int i = 0; i < testContext.getConfig().getSaveCheckTick(); i ++){
            tickDriver.tickManually();
        }
        Thread.sleep(200);
        assertEquals(1, messageExecutorForTest.requestList.size());
        Proto.Request request = messageExecutorForTest.requestList.get(0);
        assertEquals(Proto.MessageType.Save, request.getMessageType());
        assertTrue(request.hasSaveRequest());
        testContext.getGlobalState().getWriteCount().addAndGet(testContext.getConfig().getSaveCheckLimit());
        for(int i = 0; i < testContext.getConfig().getSaveCheckTick(); i ++){
            tickDriver.tickManually();
        }
        Thread.sleep(200);
        assertEquals(2, messageExecutorForTest.requestList.size());
        Proto.Request request2 = messageExecutorForTest.requestList.get(1);
        assertEquals(Proto.MessageType.Save, request2.getMessageType());
        assertTrue(request2.hasSaveRequest());
    }

    /**
     * 当tick到足够的次数后但更新次数不达标时，不能够发出命令
     * */
    @Test
    public void testSendRequest2() throws Exception {
        testContext.getGlobalState().getWriteCount().set(testContext.getConfig().getSaveCheckLimit() - 1);
        for(int i = 0; i < testContext.getConfig().getSaveCheckTick(); i ++){
            tickDriver.tickManually();
        }
        Thread.sleep(200);
        assertEquals(0, messageExecutorForTest.requestList.size());
    }

    @After
    public void tearDown() throws Exception {
    }
}