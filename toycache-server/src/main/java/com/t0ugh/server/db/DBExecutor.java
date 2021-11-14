package com.t0ugh.server.db;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;
import com.t0ugh.server.executor.MessageExecutor;
import com.t0ugh.server.callback.Callback;
import com.t0ugh.server.utils.DBUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DBExecutor extends AbstractMessageExecutor {

    public DBExecutor(GlobalContext globalContext) {
        super(globalContext, Executors.newSingleThreadExecutor());
    }


    @Override
    public void submit(Proto.Request request, Callback... callbacks) {
        checkRequest(request);
        super.submit(request, callbacks);
    }

    public void submit(Proto.Request request){
        checkRequest(request);
        super.submit(request);
    }

    @Override
    public void submitAndWait(Proto.Request request, Callback... callbacks) throws Exception {
        checkRequest(request);
        super.submitAndWait(request, callbacks);
    }

    public void submitAndWait(Proto.Request request) throws Exception{
        checkRequest(request);
        super.submitAndWait(request);
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws IOException {
        Proto.InnerSaveRequest saveRequest = request.getInnerSaveRequest();
        DBUtils.writeToFile(saveRequest.getDb(), saveRequest.getFilePath());
        getGlobalContext().getMemoryOperationExecutor().submit(newSaveRequest());
        return null;
    }

    private void checkRequest(Proto.Request request){
        if(!Objects.equals(Proto.MessageType.InnerSave, request.getMessageType())
                ||!request.hasInnerSaveRequest()
                ||!request.getInnerSaveRequest().hasDb()
                ||Strings.isNullOrEmpty(request.getInnerSaveRequest().getFilePath()))
            throw new InvalidParameterException();
    }

    private static Proto.Request newSaveRequest(){
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Save)
                .setSaveRequest(Proto.SaveRequest.newBuilder()
                        .setSaveType(Proto.SaveType.SaveTypeFinish)
                        .build())
                .build();
    }
}
