package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.InnerSaveFinish, checkExpire = false, handlerType= HandlerType.Other)
public class InnerSaveFinishHandler extends AbstractGenericsHandler<Proto.InnerSaveFinishRequest, Proto.InnerSaveFinishResponse> {

    public InnerSaveFinishHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.InnerSaveFinishResponse doHandle(Proto.InnerSaveFinishRequest unused) throws Exception {
        getGlobalContext().getGlobalState().setSaveState(SaveState.Idle);
        return Proto.InnerSaveFinishResponse.newBuilder().setOk(true).build();
    }
}
