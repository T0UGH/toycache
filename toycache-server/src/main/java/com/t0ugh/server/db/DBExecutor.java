package com.t0ugh.server.db;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;
import com.t0ugh.server.utils.DBUtils;
import com.t0ugh.server.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.concurrent.Executors;

@Slf4j
public class DBExecutor extends AbstractMessageExecutor {

    public DBExecutor(GlobalContext globalContext) {
        super(globalContext, Executors.newSingleThreadExecutor());
    }

    @Override
    public void beforeSubmit(Proto.Request request){
        checkRequest(request);
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws IOException {
        Proto.InnerSaveRequest saveRequest = request.getInnerSaveRequest();
        DBUtils.writeToFile(saveRequest.getDb(), saveRequest.getFilePath());
        getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerSaveFinishRequest());
        return null;
    }

    private void checkRequest(Proto.Request request){
        if(!Objects.equals(Proto.MessageType.InnerSave, request.getMessageType())
                ||!request.hasInnerSaveRequest()
                ||!request.getInnerSaveRequest().hasDb()
                ||Strings.isNullOrEmpty(request.getInnerSaveRequest().getFilePath()))
            throw new InvalidParameterException();
    }
}
