package com.t0ugh.server;

import com.t0ugh.server.config.Config;
import com.t0ugh.server.executor.MessageExecutor;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.rollbacker.RollBackerFactory;
import com.t0ugh.server.storage.RequestBuffer;
import com.t0ugh.server.storage.RequestRollBackers;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.tick.TickDriver;
import com.t0ugh.server.zookeeper.ZKState;
import lombok.Builder;
import lombok.Data;
import org.apache.zookeeper.ZooKeeper;

import java.io.OutputStream;

@Data
@Builder
public class GlobalContext {
    private Config config;
    private Storage storage;
    private MessageExecutor memoryOperationExecutor;
    private MessageExecutor dbExecutor;
    private MessageExecutor sendSyncExecutor;
    private MessageExecutor createToyCacheClientExecutor;
    private MessageExecutor createFollowerToZKExecutor;
    private HandlerFactory handlerFactory;
    private GlobalState globalState;
    private OutputStream writeLogOutputStream;
    private MessageExecutor writeLogExecutor;
    private RollBackerFactory rollBackerFactory;
    private TickDriver tickDriver;
    private RequestBuffer requestBuffer;
    private RequestRollBackers requestRollBackers;
    private ZooKeeper zooKeeper;
    private ZKState zkState;
}
