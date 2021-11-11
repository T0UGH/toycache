package com.t0ugh.server.handler;

import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.MessageExecutor;
import com.t0ugh.server.config.Configs;
import com.t0ugh.server.storage.MemoryStorage;
import com.t0ugh.server.storage.Storage;
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
        Storage storage = new MemoryStorage();
        GlobalContext globalContext = GlobalContext.builder()
                .storage(storage)
                .config(Configs.newDefaultConfig()).build();
        HandlerFactory handlerFactory = new HandlerFactory(globalContext);
        assertNotEquals(0, handlerFactory.size());
    }
}