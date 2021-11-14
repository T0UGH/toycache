package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.InnerSaveFinish, checkExpire = false)
public class InnerSaveFinishHandler extends AbstractHandler {

    public InnerSaveFinishHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        getGlobalContext().getGlobalState().setSaveState(SaveState.Idle);
        responseBuilder.setSaveResponse(Proto.SaveResponse.newBuilder().setOk(true));
    }
}
