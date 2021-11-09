package com.t0ugh.server;

import com.t0ugh.server.config.Configs;
import com.t0ugh.server.storage.MemoryStorage;
import com.t0ugh.server.storage.Storage;

public class Main {
    public static void main(String[] args) {
        Storage storage = new MemoryStorage();
        GlobalContext globalContext = GlobalContext.builder()
                .storage(storage)
                .config(Configs.newDefaultConfig()).build();
        MessageExecutor messageExecutor = new MessageExecutor(globalContext);
        globalContext.setMessageExecutor(messageExecutor);
        NettyServer nettyServer = new NettyServer(globalContext);
        nettyServer.runServer();
        // todo 注册一个hook来把它们都关闭
    }
}