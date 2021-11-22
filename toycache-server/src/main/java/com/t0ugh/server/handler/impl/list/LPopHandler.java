package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.LPop, handlerType= HandlerType.Write)
public class LPopHandler extends AbstractGenericsHandler<Proto.LPopRequest, Proto.LPopResponse> {

    public LPopHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LPopResponse doHandle(Proto.LPopRequest lPopRequest) throws Exception {
        Proto.LPopResponse.Builder lb = Proto.LPopResponse.newBuilder();
        getGlobalContext().getStorage().lPop(lPopRequest.getKey()).ifPresent(lb::setValue);
        return lb.build();
    }
}
