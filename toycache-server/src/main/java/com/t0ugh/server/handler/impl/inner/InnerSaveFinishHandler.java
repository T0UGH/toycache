package com.t0ugh.server.handler.impl.inner;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

import java.util.List;

@HandlerAnnotation(messageType = Proto.MessageType.InnerSaveFinish, checkExpire = false, handlerType= HandlerType.Other)
public class InnerSaveFinishHandler extends AbstractGenericsHandler<Proto.InnerSaveFinishRequest, Proto.InnerSaveFinishResponse> {

    public InnerSaveFinishHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.InnerSaveFinishResponse doHandle(Proto.InnerSaveFinishRequest unused) throws Exception {
        getGlobalContext().getGlobalState().setSaveState(SaveState.Idle);
        List<Proto.Request> rdbBuffer = getGlobalContext().getRdbBuffer().toList();
        getGlobalContext().getRdbBuffer().clear();
        return Proto.InnerSaveFinishResponse.newBuilder()
                .setLastWriteId(getGlobalContext().getGlobalState().getWriteCount().get())
                .setLastEpoch(getGlobalContext().getGlobalState().getEpoch())
                .addAllRequests(rdbBuffer).build();
    }
}
