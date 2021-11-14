package com.t0ugh.server.tick;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RewriteLogTickerTest extends BaseTest {

    RewriteLogTicker rewriteLogTicker;
    TickDriverTestImpl tickDriver;
    MessageExecutorTestImpl messageExecutorForTest;

    @Before
    public void setUp() throws Exception {
        messageExecutorForTest = new MessageExecutorTestImpl();
        testContext.setMemoryOperationExecutor(messageExecutorForTest);
        tickDriver = new TickDriverTestImpl(testContext);
        rewriteLogTicker = new RewriteLogTicker(testContext);
        tickDriver.register(rewriteLogTicker);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试当达到tick次数且文件大小超出限制时
     * 1.能够发出Request
     * */
    @Test
    public void test1() throws Exception {
        int size = testContext.getConfig().getRewriteLogSizeKBThreshold() * 1024;
        byte[] b = new byte[size * 2];
        Arrays.fill(b, (byte) 1);
        testContext.getWriteLogOutputStream().write(b);
        for(int i = 0; i < testContext.getConfig().getRewriteLogTick(); i ++){
            tickDriver.tickManually();
        }
        Thread.sleep(100);
        assertEquals(1, messageExecutorForTest.requestList.size());
        assertEquals(Proto.MessageType.RewriteLog, messageExecutorForTest.requestList.get(0).getMessageType());
        assertTrue(messageExecutorForTest.requestList.get(0).hasRewriteLogRequest());
    }


    /**
     * 测试当达到tick次数但文件大小没超出限制时
     * 2.不发出Request
     * */
    @Test
    public void test2() throws Exception {
        int size = testContext.getConfig().getRewriteLogSizeKBThreshold() * 1024;
        byte[] b = new byte[size / 2];
        Arrays.fill(b, (byte) 1);
        testContext.getWriteLogOutputStream().write(b);
        for(int i = 0; i < testContext.getConfig().getRewriteLogTick(); i ++){
            tickDriver.tickManually();
        }
        Thread.sleep(100);
        assertEquals(0, messageExecutorForTest.requestList.size());
    }
}