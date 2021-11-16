package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.HLen)
public class HLenHandler extends AbstractHandler {

    public HLenHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        int len = getGlobalContext().getStorage().hLen(request.getHLenRequest().getKey());
        responseBuilder.setHLenResponse(Proto.HLenResponse.newBuilder().setLen(len));
    }
}
