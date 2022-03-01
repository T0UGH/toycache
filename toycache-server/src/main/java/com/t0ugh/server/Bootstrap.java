package com.t0ugh.server;

import com.t0ugh.server.config.Config;
import com.t0ugh.server.config.Configs;
import com.t0ugh.server.db.DBExecutor;
import com.t0ugh.server.executor.MemoryOperationExecutor;
import com.t0ugh.server.executor.MessageExecutor;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.rollbacker.RollBackerFactory;
import com.t0ugh.server.storage.MemoryStorage;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.tick.DeleteKeyTicker;
import com.t0ugh.server.tick.TickDriverImpl;
import com.t0ugh.server.utils.WriteLogUtils;
import com.t0ugh.server.writeLog.WriteLogExecutor;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Bootstrap {

    @Getter
    private GlobalContext globalContext;

    public Bootstrap(){}

    public void startDev() throws FileNotFoundException {
        start(Configs.newDefaultConfig());
    }

    public void start(Config config) throws FileNotFoundException {
        Storage storage = new MemoryStorage();
        globalContext = GlobalContext.builder()
                .storage(storage)
                .config(config)
                .globalState(GlobalState.newInstance())
                .build();

        MessageExecutor messageExecutor = new MemoryOperationExecutor(globalContext);
        globalContext.setMemoryOperationExecutor(messageExecutor);
        OutputStream writeLogOutputStream = new FileOutputStream(WriteLogUtils
                .getWriteLogFilePath(globalContext.getConfig().getWriteLogBaseFilePath()), true);
        globalContext.setHandlerFactory(new HandlerFactory(globalContext));
        globalContext.setDbExecutor(new DBExecutor(globalContext));
        globalContext.setWriteLogOutputStream(writeLogOutputStream);
        globalContext.setWriteLogExecutor(new WriteLogExecutor(globalContext));
        globalContext.setRollBackerFactory(new RollBackerFactory(globalContext));

        TickDriverImpl tickDriver = new TickDriverImpl(globalContext);
        globalContext.setTickDriver(tickDriver);
        DeleteKeyTicker deleteKeyTicker = new DeleteKeyTicker(globalContext);
        tickDriver.register(deleteKeyTicker);
        tickDriver.start();

        NettyServer nettyServer = new NettyServer(globalContext);
        nettyServer.startServer();
    }

    public void startTest() throws FileNotFoundException {
        start(Configs.newTestConfig());
    }

}
