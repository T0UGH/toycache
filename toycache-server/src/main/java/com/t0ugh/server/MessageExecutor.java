package com.t0ugh.server;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.storage.Storage;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
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

    public MessageExecutor(Storage storage) {
        handlerFactory = new HandlerFactory(storage);
        executorService = Executors.newSingleThreadExecutor();
    }

    //todo callback这块应该抽象出来个人感觉
    public void submit(Proto.Request request, ChannelHandlerContext callback) {
        executorService.submit(new RunnableCommand(request, callback));
    }

    public void submit(Proto.Request request) {
        executorService.submit(new RunnableCommand(request));
    }

    public void submitAndWait(Proto.Request request, ChannelHandlerContext callback) throws ExecutionException, InterruptedException {
        executorService.submit(new RunnableCommand(request, callback)).get();
    }

    public void submitAndWait(Proto.Request request) throws ExecutionException, InterruptedException {
        executorService.submit(new RunnableCommand(request)).get();
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    private class RunnableCommand implements Runnable {

        @NonNull private final Proto.Request request;
        private ChannelHandlerContext callback;

        @Override
        public void run() {
            Handler handler = handlerFactory.getHandler(request.getMessageType()).orElseThrow(UnsupportedOperationException::new);
            Proto.Response response = handler.handle(request);
            // todo 以后有可能有很多callback需要调用
            if (!Objects.isNull(callback)){
                callback.write(response);
            }
        }
    }
}
