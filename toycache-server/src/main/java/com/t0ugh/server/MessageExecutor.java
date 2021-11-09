package com.t0ugh.server;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.callback.Callback;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerFactory;
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
public class MessageExecutor {

    private final ExecutorService executorService;

    private final HandlerFactory handlerFactory;

    public MessageExecutor(GlobalContext globalContext) {
        handlerFactory = new HandlerFactory(globalContext);
        executorService = Executors.newSingleThreadExecutor();
    }

    //todo callback这块应该抽象出来个人感觉
    public void submit(Proto.Request request, Callback... callbacks) {
        executorService.submit(new RunnableCommand(request, callbacks));
    }

    public void submit(Proto.Request request) {
        executorService.submit(new RunnableCommand(request));
    }

    public void submitAndWait(Proto.Request request, Callback... callbacks) throws ExecutionException, InterruptedException {
        executorService.submit(new RunnableCommand(request, callbacks)).get();
    }

    public void submitAndWait(Proto.Request request) throws ExecutionException, InterruptedException {
        executorService.submit(new RunnableCommand(request)).get();
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    private class RunnableCommand implements Runnable {

        @NonNull private final Proto.Request request;
        private Callback[] callbacks = new Callback[0];

        @Override
        public void run() {
            Handler handler = handlerFactory.getHandler(request.getMessageType()).orElseThrow(UnsupportedOperationException::new);
            Proto.Response response = handler.handle(request);
            // todo 以后有可能有很多callback需要调用
            // todo 这个tryCatch块有办法没
            Arrays.stream(callbacks).forEach(callback -> {
                callback.callback(request, response);
            });
        }
    }
}
