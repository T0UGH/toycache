package com.t0ugh.server.handler.impl.control;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.DBUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.Save, checkExpire = false, handlerType= HandlerType.Other)
public class SaveHandler extends AbstractGenericsHandler<Proto.SaveRequest, Proto.SaveResponse> {

    public SaveHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SaveResponse doHandle(Proto.SaveRequest unused) throws Exception {
        if (Objects.equals(SaveState.Running, getGlobalContext().getGlobalState().getSaveState())){
            return Proto.SaveResponse.newBuilder().setOk(false).build();
        }
        getGlobalContext().getGlobalState().setSaveState(SaveState.Running);
        getGlobalContext().getRdbBuffer().clear();
        Set<String> keys = getGlobalContext().getStorage().keys();
        Proto.Request dbRequest = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerStartSave)
                .setInnerStartSaveRequest(Proto.InnerStartSaveRequest.newBuilder()
                        .setFilePath(DBUtils.genFilePath(getGlobalContext().getConfig().getDbBaseFilePath()))
                        .putAllExpire(getGlobalContext().getStorage().cloneExpires())
                        .addAllKeys(keys)
                        .build())
                .build();
        getGlobalContext().getDbExecutor().submit(dbRequest);
        return Proto.SaveResponse.newBuilder().setOk(true).build();
    }

}
