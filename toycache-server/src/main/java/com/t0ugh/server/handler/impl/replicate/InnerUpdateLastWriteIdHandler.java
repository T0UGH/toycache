package com.t0ugh.server.handler.impl.replicate;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.GlobalState;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.InnerUpdateLastWriteId, checkExpire = false, handlerType= HandlerType.Other)
public class InnerUpdateLastWriteIdHandler extends AbstractGenericsHandler<Proto.InnerUpdateLastWriteIdRequest, Proto.InnerUpdateLastWriteIdResponse> {

    public InnerUpdateLastWriteIdHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.InnerUpdateLastWriteIdResponse doHandle(Proto.InnerUpdateLastWriteIdRequest req) throws Exception {
        GlobalState state = getGlobalContext().getGlobalState();
        if (Objects.equals(state.getGroupId(), req.getClusterId())){
            long originLastWriteId = state.getFollowerProcess().get(req.getServerId());
            state.getFollowerProcess().put(req.getServerId(), Long.max(originLastWriteId, req.getLastWriteId()));
        }
        return Proto.InnerUpdateLastWriteIdResponse.newBuilder().build();
    }
}
