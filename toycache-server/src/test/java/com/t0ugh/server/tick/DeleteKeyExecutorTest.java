package com.t0ugh.server.tick;

import com.t0ugh.server.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeleteKeyExecutorTest extends BaseTest {

    DeleteKeyExecutor deleteKeyExecutor;
    Ticker ticker;
    MessageExecutorTestImpl messageExecutorForTest;

    @Before
    public void setUp() throws Exception {
        messageExecutorForTest = new MessageExecutorTestImpl();
        testContext.setMessageExecutor(messageExecutorForTest);
        ticker = new Ticker(testContext);
        deleteKeyExecutor = new DeleteKeyExecutor(testContext);
        ticker.register(deleteKeyExecutor);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test0() throws Exception {
        int periodicalDeleteTick = testContext.getConfig().getPeriodicalDeleteTick();
        int tickInterval = testContext.getConfig().getTickInterval();
        ticker.start();
        Thread.sleep(periodicalDeleteTick * tickInterval * 2L);
        assertTrue(messageExecutorForTest.requestList.size() > 0);
    }
}