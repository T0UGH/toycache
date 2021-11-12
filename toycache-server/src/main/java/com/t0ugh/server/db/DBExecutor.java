package com.t0ugh.server.db;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.DBUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DBExecutor {

    private final GlobalContext globalContext;

    private final ExecutorService executorService;

    public DBExecutor(GlobalContext globalContext) {
        this.globalContext = globalContext;
        executorService = Executors.newSingleThreadExecutor();
    }


    public void submit(Proto.Request request){
        checkRequest(request);
        executorService.submit(new DBRunnable(request));
    }

    public void submitAndWait(Proto.Request request) throws Exception{
        checkRequest(request);
        executorService.submit(new DBRunnable(request)).get();
    }

    private void checkRequest(Proto.Request request){
        if(!Objects.equals(Proto.MessageType.InnerSave, request.getMessageType())
                ||!request.hasInnerSaveRequest()
                ||!request.getInnerSaveRequest().hasDb()
                ||Strings.isNullOrEmpty(request.getInnerSaveRequest().getFilePath()))
            throw new InvalidParameterException();
    }

    @AllArgsConstructor
    private class DBRunnable implements Runnable {

        Proto.Request request;

        @Override
        public void run() {
            try {
                Proto.InnerSaveRequest saveRequest = request.getInnerSaveRequest();
                DBUtils.writeToFile(saveRequest.getDb(), saveRequest.getFilePath());
                // 把消息通知回去
                globalContext.getMessageExecutor().submit(newSaveRequest());
            } catch (IOException e) {
                log.error("IO", e);
                e.printStackTrace();
            }
        }

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
