package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.SRem, isWrite = true)
public class SRemHandler extends AbstractHandler {

    public SRemHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.SRemRequest req = request.getSRemRequest();
        int deleted = getGlobalContext().getStorage().sRem(req.getKey(), Sets.newHashSet(req.getMembersList()));
        responseBuilder.setSRemResponse(Proto.SRemResponse.newBuilder().setDeleted(deleted));
    }
}
