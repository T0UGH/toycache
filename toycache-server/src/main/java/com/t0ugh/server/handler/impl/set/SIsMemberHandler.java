package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(messageType = Proto.MessageType.SIsMember, handlerType= HandlerType.Read)
public class SIsMemberHandler extends AbstractGenericsHandler<Proto.SIsMemberRequest, Proto.SIsMemberResponse> {

    public SIsMemberHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SIsMemberResponse doHandle(Proto.SIsMemberRequest req) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(req.getMember());
        boolean isMember = getGlobalContext().getStorage().sIsMember(req.getKey(), req.getMember());
        return Proto.SIsMemberResponse.newBuilder().setIsMember(isMember).build();
    }
}
