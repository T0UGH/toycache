package com.t0ugh.server.executor;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.sdk.callback.Callback;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

@Slf4j
public abstract class AbstractMessageExecutor implements MessageExecutor{

    private final GlobalContext globalContext;
    private final ExecutorService executorService;

    protected AbstractMessageExecutor(GlobalContext globalContext, ExecutorService executorService) {
        this.globalContext = globalContext;
        this.executorService = executorService;
    }

    public void submit(Proto.Request request, Callback... callbacks){
        try{
            beforeSubmit(request);
            executorService.submit(new RunnableCommand(request, callbacks));
        } catch (RuntimeException e){
            handleException(request, e, callbacks);
        }
    }

    public void submit(Proto.Request request){
        try{
            beforeSubmit(request);
            executorService.submit(new RunnableCommand(request));
        } catch (RuntimeException e){
            handleException(request, e);
        }
    }

    public void submitAndWait(Proto.Request request, Callback... callbacks) throws Exception{
        try{
            beforeSubmit(request);
            executorService.submit(new RunnableCommand(request, callbacks)).get();
        } catch (RuntimeException e){
            handleException(request, e, callbacks);
        }
    }

    public void submitAndWait(Proto.Request request) throws Exception{
        beforeSubmit(request);
        executorService.submit(new RunnableCommand(request)).get();
        try{
            beforeSubmit(request);
            executorService.submit(new RunnableCommand(request)).get();
        } catch (RuntimeException e){
            handleException(request, e);
        }
    }

    public void shutdown(){
        executorService.shutdown();
    }

    protected GlobalContext getGlobalContext(){
        return globalContext;
    }

    protected ExecutorService getExecutorService(){
        return executorService;
    }

    protected void beforeSubmit(Proto.Request request){

    }

    protected void handleException(Proto.Request request, RuntimeException runtimeException, Callback... callbacks){

    }

    public abstract Proto.Response doRequest(Proto.Request request) throws Exception;

    @RequiredArgsConstructor
    @AllArgsConstructor
    private class RunnableCommand implements Runnable {

        @NonNull
        private final Proto.Request request;
        private Callback[] callbacks = new Callback[0];

        @Override
        public void run() {
            try{
                Proto.Response response = doRequest(request);
                Arrays.stream(callbacks).forEach(callback -> {
                    callback.callback(request, response);
                });
            } catch (Exception e){
                //todo 这样处理Exception太暴力了
                log.error("RunnableCommand", e);
                e.printStackTrace();
            }

        }
    }
}
