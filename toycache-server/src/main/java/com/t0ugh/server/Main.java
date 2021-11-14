package com.t0ugh.server;

import com.t0ugh.server.config.Configs;
import com.t0ugh.server.executor.MemoryOperationExecutor;
import com.t0ugh.server.executor.MessageExecutor;
import com.t0ugh.server.storage.MemoryDBStorage;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.tick.DeleteKeyTicker;
import com.t0ugh.server.tick.TickDriverImpl;

public class Main {
    public static void main(String[] args) {

        Storage storage = new MemoryDBStorage();
        GlobalContext globalContext = GlobalContext.builder()
                .storage(storage)
                .config(Configs.newDefaultConfig()).build();

        MessageExecutor messageExecutor = new MemoryOperationExecutor(globalContext);
        globalContext.setMemoryOperationExecutor(messageExecutor);

        TickDriverImpl tickDriver = new TickDriverImpl(globalContext);
        DeleteKeyTicker deleteKeyTicker = new DeleteKeyTicker(globalContext);
        tickDriver.register(deleteKeyTicker);
        tickDriver.start();

        NettyServer nettyServer = new NettyServer(globalContext);
        nettyServer.runServer();
        // todo 注册一个hook来把它们都关闭
    }
}
