package com.t0ugh.server.handler.impl.check.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;

import java.util.Objects;
import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.SMembers, handlerType= HandlerType.Check)
public class CheckSMembers extends AbstractCheckHandler<Proto.CheckSMembersRequest, Proto.CheckSMembersResponse> {

    public CheckSMembers(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckSMembersResponse doCheckHandle(Proto.CheckSMembersRequest req) throws Exception {
        Set<String> actualMembers = getGlobalContext().getStorage().sMembers(req.getKey());
        Proto.CheckSMembersResponse.Builder builder = Proto.CheckSMembersResponse.newBuilder();
        builder.setPass(Objects.equals(Sets.newHashSet(req.getSetValueList()), actualMembers));
        return builder.addAllActualSetValue(actualMembers).build();
    }
}
