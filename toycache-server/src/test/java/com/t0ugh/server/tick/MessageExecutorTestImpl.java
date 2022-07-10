package com.t0ugh.server.tick;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.executor.MessageExecutor;
import com.t0ugh.sdk.callback.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MessageExecutorTestImpl implements MessageExecutor {
    public List<Proto.Request> requestList;
    public MessageExecutorTestImpl() {
        requestList = new ArrayList<>();
    }

    @Override
    public Future<Proto.Response> submit(Proto.Request request) {
        requestList.add(request);
        return null;
    }

    @Override
    public Future<Proto.Response> submit(Proto.Request request, Callback... callbacks) {
        requestList.add(request);
        return null;
    }

    @Override
    public void submitAndWait(Proto.Request request) throws ExecutionException, InterruptedException {
        requestList.add(request);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void submitAndWait(Proto.Request request, Callback... callbacks) throws ExecutionException, InterruptedException {
        requestList.add(request);
    }
}
