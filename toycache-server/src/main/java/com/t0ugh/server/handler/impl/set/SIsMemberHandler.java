package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.SCard)
public class SIsMemberHandler extends AbstractHandler {

    public SIsMemberHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws ValueTypeNotMatchException {
        Proto.SIsMemberRequest req = request.getSIsMemberRequest();
        boolean isMember = getGlobalContext().getStorage().sIsMember(req.getKey(), req.getMember());
        responseBuilder.setSIsMemberResponse(Proto.SIsMemberResponse.newBuilder().setIsMember(isMember).build());
    }
}
