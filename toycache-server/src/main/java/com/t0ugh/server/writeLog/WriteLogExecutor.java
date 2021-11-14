package com.t0ugh.server.writeLog;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;
import com.t0ugh.server.executor.MessageExecutor;
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
public class WriteLogExecutor extends AbstractMessageExecutor {

    public WriteLogExecutor(GlobalContext globalContext){
        super(globalContext, Executors.newSingleThreadExecutor());
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws IOException {
        request.writeDelimitedTo(getGlobalContext().getWriteLogOutputStream());
        // todo: 这个return null是不是不太好
        return null;
    }
}
