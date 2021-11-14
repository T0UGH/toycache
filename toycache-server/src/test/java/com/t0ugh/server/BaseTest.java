package com.t0ugh.server;

import com.t0ugh.server.config.Configs;
import com.t0ugh.server.db.DBExecutor;
import com.t0ugh.server.executor.MemoryOperationExecutor;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.storage.MemoryDBStorage;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.WriteLogUtils;
import com.t0ugh.server.writeLog.WriteLogExecutor;
import org.junit.After;
import org.junit.Before;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class BaseTest {

    public GlobalContext testContext;

    @Before
    public void setUpBase() throws Exception {

        Storage storage = new MemoryDBStorage();
        testContext = GlobalContext.builder()
                .storage(storage)
                .config(Configs.newTestConfig())
                .globalState(GlobalState.newInstance())
                .build();
        // todo: 因为是测试所以append=false
        OutputStream writeLogOutputStream = new FileOutputStream(WriteLogUtils
                .getWriteLogFilePath(testContext.getConfig().getWriteLogBaseFilePath()), false);
        testContext.setMemoryOperationExecutor(new MemoryOperationExecutor(testContext));
        testContext.setHandlerFactory(new HandlerFactory(testContext));
        testContext.setDbExecutor(new DBExecutor(testContext));
        testContext.setWriteLogOutputStream(writeLogOutputStream);
        testContext.setWriteLogExecutor(new WriteLogExecutor(testContext));
    }

    @After
    public void tearDownBase() throws Exception {
    }

}
