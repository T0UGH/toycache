package com.t0ugh.server.handler.impl.check.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.SIsMember, handlerType= HandlerType.Check)
public class CheckSIsMember extends AbstractCheckHandler<Proto.CheckSIsMemberRequest, Proto.CheckSIsMemberResponse> {

    public CheckSIsMember(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.CheckSIsMemberResponse doCheckHandle(Proto.CheckSIsMemberRequest req) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(req.getMember());
        Proto.CheckSIsMemberResponse.Builder builder = Proto.CheckSIsMemberResponse.newBuilder();
        boolean isMember = getGlobalContext().getStorage().sIsMember(req.getKey(), req.getMember());
        builder.setPass(Objects.equals(isMember, req.getIsMember()));
        return builder.setActualIsMember(isMember).build();
    }
}
