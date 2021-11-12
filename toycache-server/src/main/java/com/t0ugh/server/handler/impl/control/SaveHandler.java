package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.DBUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@HandlerAnnotation(type = Proto.MessageType.Save, checkExpire = false, isWrite = false)
public class SaveHandler extends AbstractHandler {

    //这里的setter和getter仅供测试代码使用
    @Getter
    @Setter
    private SaveState saveState;

    public SaveHandler(GlobalContext globalContext) {
        super(globalContext);
        saveState = SaveState.Idle;
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws InvalidParamException {
        switch (request.getSaveRequest().getSaveType()) {
            case SaveTypeStart:
                doStart(request, responseBuilder);
                break;
            case SaveTypeFinish:
                doFinish(request, responseBuilder);
                break;
            default:
                throw new InvalidParamException();
        }
    }

    private void doStart(Proto.Request unused, Proto.Response.Builder responseBuilder) {
        if (Objects.equals(SaveState.Running, saveState)){
            responseBuilder.setSaveResponse(Proto.SaveResponse.newBuilder().setOk(false));
            return;
        }
        saveState = SaveState.Running;
        Proto.Request dbRequest = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerSave)
                .setInnerSaveRequest(Proto.InnerSaveRequest.newBuilder()
                        .setDb(getGlobalContext().getStorage().toUnModifiableDB())
                        .setFilePath(DBUtils.genFilePath(getGlobalContext().getConfig().getDbBaseFilePath()))
                        .build())
                .build();
        getGlobalContext().getDbExecutor().submit(dbRequest);
        responseBuilder.setSaveResponse(Proto.SaveResponse.newBuilder().setOk(true));
    }

    private void doFinish(Proto.Request unused, Proto.Response.Builder responseBuilder) {
        saveState = SaveState.Idle;
        responseBuilder.setSaveResponse(Proto.SaveResponse.newBuilder().setOk(true));
    }
}
