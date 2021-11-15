package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LPop, isWrite = true)
public class LPopHandler extends AbstractHandler {

    public LPopHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.LPopResponse.Builder lb = Proto.LPopResponse.newBuilder();
        getGlobalContext().getStorage().lPop(request.getLPopRequest().getKey()).ifPresent(lb::setValue);
        responseBuilder.setLPopResponse(lb);
    }
}
