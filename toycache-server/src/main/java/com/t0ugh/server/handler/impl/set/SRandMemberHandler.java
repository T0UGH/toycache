package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Set;

@HandlerAnnotation(type = Proto.MessageType.SRandMember)
public class SRandMemberHandler extends AbstractHandler {

    public SRandMemberHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.SRandMemberRequest req = request.getSRandMemberRequest();
        Set<String> setValue = getGlobalContext().getStorage().sRandMember(req.getKey(), req.getCount());
        responseBuilder.setSRandMemberResponse(Proto.SRandMemberResponse.newBuilder().addAllSetValue(setValue));
    }
}
