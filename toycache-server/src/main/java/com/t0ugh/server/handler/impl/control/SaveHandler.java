package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.DBUtils;

import java.util.Objects;

@HandlerAnnotation(type = Proto.MessageType.Save, checkExpire = false)
public class SaveHandler extends AbstractHandler {

    public SaveHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws InvalidParamException {
        if (Objects.equals(SaveState.Running, getGlobalContext().getGlobalState().getSaveState())){
            responseBuilder.setSaveResponse(Proto.SaveResponse.newBuilder().setOk(false));
            return;
        }
        getGlobalContext().getGlobalState().setSaveState(SaveState.Running);
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

//    private void doFinish(Proto.Request unused, Proto.Response.Builder responseBuilder) {
//        saveState = SaveState.Idle;
//        responseBuilder.setSaveResponse(Proto.SaveResponse.newBuilder().setOk(true));
//    }
}
