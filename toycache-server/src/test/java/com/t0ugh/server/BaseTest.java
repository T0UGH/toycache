package com.t0ugh.server;

import com.t0ugh.server.config.Configs;
import com.t0ugh.server.db.DBExecutor;
import com.t0ugh.server.executor.MemoryOperationExecutor;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.storage.ExpireMap;
import com.t0ugh.server.storage.MemoryDBStorage;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.writeLog.WriteLogExecutor;
import org.junit.After;
import org.junit.Before;

public class BaseTest {

    public GlobalContext testContext;

    @Before
    public void setUpBase() throws Exception {
        Storage storage = new MemoryDBStorage();
        testContext = GlobalContext.builder()
                .storage(storage)
                .expireMap(new ExpireMap())
                .config(Configs.newTestConfig())
                .globalState(GlobalState.newInstance())
                .build();
        testContext.setMemoryOperationExecutor(new MemoryOperationExecutor(testContext));
        testContext.setHandlerFactory(new HandlerFactory(testContext));
        testContext.setDbExecutor(new DBExecutor(testContext));
        testContext.setWriteLogExecutor(new WriteLogExecutor(testContext));
    }

    @After
    public void tearDownBase() throws Exception {
    }

}