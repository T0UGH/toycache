package com.t0ugh.server.tick;

import com.t0ugh.server.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TickerTest extends BaseTest {

    Ticker ticker;

    TickableTestImpl tickableTestImpl;

    @Before
    public void setUp() throws Exception {
        ticker = new Ticker(testContext);
        tickableTestImpl = new TickableTestImpl();
        ticker.register(tickableTestImpl);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testTicker() throws Exception {
        ticker.start();
        int interval = testContext.getConfig().getTickInterval();
        Thread.sleep(interval * 5L);
        ticker.shutdown();
        assertTrue(tickableTestImpl.count > 0);
    }
}