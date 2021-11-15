package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LLen)
public class LLenHandler extends AbstractHandler {

    public LLenHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        int len = getGlobalContext().getStorage().lLen(request.getLLenRequest().getKey());
        responseBuilder.setLLenResponse(Proto.LLenResponse.newBuilder().setCount(len));
    }
}
