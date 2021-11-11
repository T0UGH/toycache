package com.t0ugh.server;

import com.t0ugh.server.config.Configs;
import com.t0ugh.server.storage.ExpireMap;
import com.t0ugh.server.storage.MemoryStorage;
import com.t0ugh.server.storage.Storage;
import org.junit.After;
import org.junit.Before;

public class BaseTest {

    public GlobalContext testContext;

    @Before
    public void setUpBase() throws Exception {
        Storage storage = new MemoryStorage();
        testContext = GlobalContext.builder()
                .storage(storage)
                .expireMap(new ExpireMap())
                .config(Configs.newTestConfig()).build();
        MessageExecutor messageExecutor = new MessageExecutorImpl(testContext);
        testContext.setMessageExecutor(messageExecutor);
    }

    @After
    public void tearDownBase() throws Exception {
    }

}
