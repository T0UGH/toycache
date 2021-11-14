package com.t0ugh.server.executor;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.callback.Callback;
import com.t0ugh.server.handler.Handler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 命令执行器, 命令都在一个单独的线程中执行
 * */
@Slf4j
public class MemoryOperationExecutor extends AbstractMessageExecutor {

    public MemoryOperationExecutor(GlobalContext globalContext) {
        super(globalContext, Executors.newSingleThreadExecutor());
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) {
        Handler handler = getGlobalContext().getHandlerFactory()
                .getHandler(request.getMessageType())
                .orElseThrow(UnsupportedOperationException::new);
        return handler.handle(request);
    }
}
