package com.t0ugh.server;

import com.t0ugh.server.config.Config;
import com.t0ugh.server.executor.MessageExecutor;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.rollbacker.RollBackerFactory;
import com.t0ugh.server.storage.Storage;
import lombok.Builder;
import lombok.Data;

import java.io.OutputStream;

@Data
@Builder
public class GlobalContext {
    private Config config;
    private Storage storage;
    private MessageExecutor memoryOperationExecutor;
    private MessageExecutor dbExecutor;
    private HandlerFactory handlerFactory;
    private GlobalState globalState;
    private OutputStream writeLogOutputStream;
    private MessageExecutor writeLogExecutor;
    private RollBackerFactory rollBackerFactory;
}
