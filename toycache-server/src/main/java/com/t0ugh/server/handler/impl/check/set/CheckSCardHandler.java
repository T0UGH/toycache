package com.t0ugh.server.handler.impl.check.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.SCard, handlerType= HandlerType.Check)
public class CheckSCardHandler extends AbstractCheckHandler<Proto.CheckSCardRequest, Proto.CheckSCardResponse> {

    public CheckSCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckSCardResponse doCheckHandle(Proto.CheckSCardRequest req) throws Exception {
        int actualSize = getGlobalContext().getStorage().sCard(req.getKey());
        Proto.CheckSCardResponse.Builder builder = Proto.CheckSCardResponse.newBuilder();
        builder.setPass(Objects.equals(req.getSize(), actualSize));
        return builder.setActualSize(actualSize).build();
    }
}
