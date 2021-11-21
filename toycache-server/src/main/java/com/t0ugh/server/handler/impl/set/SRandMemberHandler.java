package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.SRandMember, handlerType= HandlerType.Read)
public class SRandMemberHandler extends AbstractHandler<Proto.SRandMemberRequest, Proto.SRandMemberResponse> {

    public SRandMemberHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SRandMemberResponse doHandle(Proto.SRandMemberRequest req) throws Exception {
        MessageUtils.assertIntNotNegative(req.getCount());
        Set<String> setValue = getGlobalContext().getStorage().sRandMember(req.getKey(), req.getCount());
        return Proto.SRandMemberResponse.newBuilder().addAllSetValue(setValue).build();
    }
}
