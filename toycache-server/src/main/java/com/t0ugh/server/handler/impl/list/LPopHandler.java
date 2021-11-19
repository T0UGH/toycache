package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LPop, isWrite = true)
public class LPopHandler extends AbstractHandler<Proto.LPopRequest, Proto.LPopResponse> {

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
