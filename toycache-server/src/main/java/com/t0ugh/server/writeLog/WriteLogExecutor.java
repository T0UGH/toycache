package com.t0ugh.server.writeLog;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.MessageExecutor;
import com.t0ugh.server.callback.Callback;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class WriteLogExecutor implements MessageExecutor {

    private final GlobalContext globalContext;

    private final ExecutorService executorService;

    public WriteLogExecutor(GlobalContext globalContext){
        this.globalContext = globalContext;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void submit(Proto.Request request, Callback... callbacks) {
        executorService.submit(new WriteLogRunnable(request, callbacks));
    }

    @Override
    public void submit(Proto.Request request) {
        executorService.submit(new WriteLogRunnable(request));
    }

    @Override
    public void submitAndWait(Proto.Request request, Callback... callbacks) throws Exception {
        executorService.submit(new WriteLogRunnable(request, callbacks)).get();
    }

    @Override
    public void submitAndWait(Proto.Request request) throws Exception {
        executorService.submit(new WriteLogRunnable(request)).get();
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    private class WriteLogRunnable implements Runnable {

        @NonNull Proto.Request request;
        private Callback[] callbacks = new Callback[0];

        @Override
        public void run() {
            try {
                request.writeDelimitedTo(globalContext.getWriteLogOutputStream());
                Arrays.stream(callbacks).forEach(callback -> {
                    callback.callback(request, null);
                });
            } catch (IOException e) {
                log.error("IO", e);
                e.printStackTrace();
            }
        }

    }
}
