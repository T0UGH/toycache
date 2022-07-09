package com.t0ugh.server.executor;

import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 命令执行器, 命令都在一个单独的线程中执行
 * */
@Slf4j
public class MemoryOperationExecutor extends AbstractMessageExecutor {

    public MemoryOperationExecutor(GlobalContext globalContext) {
        super(globalContext, new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(10000)));
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) {
        Handler handler = getGlobalContext().getHandlerFactory()
                .getHandler(request.getMessageType())
                .orElseThrow(UnsupportedOperationException::new);
        return handler.handle(request);
    }

    @Override
    protected void handleException(Proto.Request request, RuntimeException runtimeException, Callback... callbacks){
        Proto.Response response = MessageUtils.responseWithCode(Proto.ResponseCode.ServerBusy, request.getClientTId());
        Arrays.stream(callbacks).forEach(callback -> {
            callback.callback(request, response);
        });
    }
}
