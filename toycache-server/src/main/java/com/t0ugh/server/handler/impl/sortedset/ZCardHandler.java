package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.ZCard)
public class ZCardHandler extends AbstractHandler {

    public ZCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ZCardRequest req = request.getZCardRequest();
        int count = getGlobalContext().getStorage().zCard(req.getKey());
        responseBuilder.setZCardResponse(Proto.ZCardResponse.newBuilder().setCount(count));
    }
}
