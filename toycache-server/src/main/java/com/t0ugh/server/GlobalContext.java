package com.t0ugh.server;

import com.t0ugh.server.config.Config;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.storage.ExpireMap;
import com.t0ugh.server.storage.Storage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalContext {
    private Config config;
    private Storage storage;
    private MessageExecutor messageExecutor;
    private ExpireMap expireMap;
    private HandlerFactory handlerFactory;
}
