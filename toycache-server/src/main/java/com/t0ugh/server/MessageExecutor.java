package com.t0ugh.server;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.callback.Callback;

import java.util.concurrent.ExecutionException;

public interface MessageExecutor {
    void submit(Proto.Request request, Callback... callbacks);

    void submit(Proto.Request request);

    void submitAndWait(Proto.Request request, Callback... callbacks) throws Exception;

    void submitAndWait(Proto.Request request) throws Exception;
}
