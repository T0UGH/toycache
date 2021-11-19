package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.InnerSaveFinish, checkExpire = false)
public class InnerSaveFinishHandler extends AbstractHandler<Proto.InnerSaveFinishRequest, Proto.InnerSaveFinishResponse> {

    public InnerSaveFinishHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.InnerSaveFinishResponse doHandle(Proto.InnerSaveFinishRequest unused) throws Exception {
        getGlobalContext().getGlobalState().setSaveState(SaveState.Idle);
        return Proto.InnerSaveFinishResponse.newBuilder().setOk(true).build();
    }
}
