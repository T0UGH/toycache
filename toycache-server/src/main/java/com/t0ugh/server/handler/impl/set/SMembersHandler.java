package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Set;

@HandlerAnnotation(type = Proto.MessageType.SMembers)
public class SMembersHandler extends AbstractHandler {

    public SMembersHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws ValueTypeNotMatchException {
        Set<String> setValue = getGlobalContext().getStorage().sMembers(request.getSMembersRequest().getKey());
        responseBuilder.setSMembersResponse(Proto.SMembersResponse.newBuilder().addAllSetValue(setValue).build());
    }
}
