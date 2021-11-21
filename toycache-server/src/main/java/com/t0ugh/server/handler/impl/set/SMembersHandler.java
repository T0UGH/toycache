package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.SMembers, handlerType= HandlerType.Read)
public class SMembersHandler extends AbstractHandler<Proto.SMembersRequest, Proto.SMembersResponse> {

    public SMembersHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SMembersResponse doHandle(Proto.SMembersRequest sMembersRequest) throws Exception {
        Set<String> setValue = getGlobalContext().getStorage().sMembers(sMembersRequest.getKey());
        return Proto.SMembersResponse.newBuilder().addAllSetValue(setValue).build();
    }
}
