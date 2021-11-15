package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LSet, isWrite = true)
public class LSetHandler extends AbstractHandler {

    public LSetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.LSetRequest req = request.getLSetRequest();
        boolean ok = getGlobalContext().getStorage().lSet(req.getKey(), req.getIndex(), req.getNewValue());
        responseBuilder.setLSetResponse(Proto.LSetResponse.newBuilder().setOk(ok));
    }
}
