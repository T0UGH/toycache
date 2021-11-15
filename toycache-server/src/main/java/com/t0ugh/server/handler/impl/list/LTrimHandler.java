package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LTrim, isWrite = true)
public class LTrimHandler extends AbstractHandler {

    public LTrimHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.LTrimRequest req = request.getLTrimRequest();
        boolean ok = getGlobalContext().getStorage().lTrim(req.getKey(), req.getStart(), req.getEnd());
        responseBuilder.setLTrimResponse(Proto.LTrimResponse.newBuilder().setOk(ok));
    }
}
