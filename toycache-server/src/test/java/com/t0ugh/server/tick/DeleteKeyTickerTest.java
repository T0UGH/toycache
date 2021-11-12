package com.t0ugh.server.tick;

import com.t0ugh.server.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeleteKeyTickerTest extends BaseTest {

    DeleteKeyTicker deleteKeyTicker;
    TickDriverImpl tickDriver;
    MessageExecutorTestImpl messageExecutorForTest;

    @Before
    public void setUp() throws Exception {
        messageExecutorForTest = new MessageExecutorTestImpl();
        testContext.setMemoryOperationExecutor(messageExecutorForTest);
        tickDriver = new TickDriverImpl(testContext);
        deleteKeyTicker = new DeleteKeyTicker(testContext);
        tickDriver.register(deleteKeyTicker);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test0() throws Exception {
        int periodicalDeleteTick = testContext.getConfig().getPeriodicalDeleteTick();
        int tickInterval = testContext.getConfig().getTickInterval();
        tickDriver.start();
        Thread.sleep(periodicalDeleteTick * tickInterval * 2L);
        assertTrue(messageExecutorForTest.requestList.size() > 0);
    }
}