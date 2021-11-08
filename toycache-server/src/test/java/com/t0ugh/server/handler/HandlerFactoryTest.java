package com.t0ugh.server.handler;

import com.t0ugh.server.storage.MemoryStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HandlerFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void registerAll() {
        HandlerFactory handlerFactory = new HandlerFactory(new MemoryStorage());
        assertNotEquals(0, handlerFactory.size());
    }
}