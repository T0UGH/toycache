package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.ZCount)
public class ZCountHandler extends AbstractHandler {

    public ZCountHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ZCountRequest req = request.getZCountRequest();
        int count = getGlobalContext().getStorage().zCount(req.getKey(), req.getMin(), req.getMax());
        responseBuilder.setZCountResponse(Proto.ZCountResponse.newBuilder().setCount(count));
    }
}
