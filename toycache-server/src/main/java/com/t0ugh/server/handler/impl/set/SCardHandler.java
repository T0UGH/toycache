package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.SCard)
public class SCardHandler extends AbstractHandler {

    public SCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws ValueTypeNotMatchException {
        Proto.SCardRequest req = request.getSCardRequest();
        int size = getGlobalContext().getStorage().sCard(req.getKey());
        responseBuilder.setSCardResponse(Proto.SCardResponse.newBuilder().setSize(size).build());
    }
}
