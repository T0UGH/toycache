package com.t0ugh.server.executor;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.sdk.callback.Callback;

import java.util.concurrent.Future;

public interface MessageExecutor {
    Future<Proto.Response> submit(Proto.Request request, Callback... callbacks);

    Future<Proto.Response> submit(Proto.Request request);

    void submitAndWait(Proto.Request request, Callback... callbacks) throws Exception;

    void submitAndWait(Proto.Request request) throws Exception;

    void shutdown();
}
