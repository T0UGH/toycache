package com.t0ugh.server;

import com.t0ugh.server.config.Config;
import com.t0ugh.server.config.Configs;
import com.t0ugh.server.db.DBExecutor;
import com.t0ugh.server.executor.MemoryOperationExecutor;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.rollbacker.RollBackerFactory;
import com.t0ugh.server.storage.MemoryStorage;
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

        Storage storage = new MemoryStorage();
        Config config = Configs.newTestConfig();
        config.setClusterId(1);
        config.setServerId(1);
        testContext = GlobalContext.builder()
                .storage(storage)
                .config(config)
                .globalState(GlobalState.newInstance(config.getServerId(), config.getClusterId()))
                .build();
        // todo: 因为是测试所以append=false
        OutputStream writeLogOutputStream = new FileOutputStream(WriteLogUtils
                .getWriteLogFilePath(testContext.getConfig().getWriteLogBaseFilePath()), false);
        testContext.setMemoryOperationExecutor(new MemoryOperationExecutor(testContext));
        testContext.setHandlerFactory(new HandlerFactory(testContext));
        testContext.setDbExecutor(new DBExecutor(testContext));
        testContext.setWriteLogOutputStream(writeLogOutputStream);
        testContext.setWriteLogExecutor(new WriteLogExecutor(testContext));
        testContext.setRollBackerFactory(new RollBackerFactory(testContext));
    }

    @After
    public void tearDownBase() throws Exception {
    }

}
