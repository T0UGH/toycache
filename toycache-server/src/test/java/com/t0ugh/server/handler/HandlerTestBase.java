package com.t0ugh.server.handler;

import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.config.Configs;
import com.t0ugh.server.storage.ExpireMap;
import com.t0ugh.server.storage.MemoryStorage;
import com.t0ugh.server.storage.Storage;
import org.junit.After;
import org.junit.Before;

public class HandlerTestBase {

    public GlobalContext testContext;

    @Before
    public void setUpBase() throws Exception {
        Storage storage = new MemoryStorage();
        testContext = GlobalContext.builder()
                .storage(storage)
                .expireMap(new ExpireMap())
                .config(Configs.newDefaultConfig()).build();
        new HandlerFactory(testContext);
    }

    @After
    public void tearDownBase() throws Exception {
    }

}
