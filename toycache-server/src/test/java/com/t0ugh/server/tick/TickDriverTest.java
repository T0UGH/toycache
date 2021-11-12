package com.t0ugh.server.tick;

import com.t0ugh.server.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TickDriverTest extends BaseTest {

    TickDriver tickDriver;

    TickerTestImpl tickableTestImpl;

    @Before
    public void setUp() throws Exception {
        tickDriver = new TickDriver(testContext);
        tickableTestImpl = new TickerTestImpl();
        tickDriver.register(tickableTestImpl);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testTicker() throws Exception {
        tickDriver.start();
        int interval = testContext.getConfig().getTickInterval();
        Thread.sleep(interval * 5L);
        tickDriver.shutdown();
        assertTrue(tickableTestImpl.count > 0);
    }
}