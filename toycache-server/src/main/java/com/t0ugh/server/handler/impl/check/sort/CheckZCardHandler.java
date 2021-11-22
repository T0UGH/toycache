package com.t0ugh.server.handler.impl.check.sort;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.ZCard, handlerType= HandlerType.Check)
public class CheckZCardHandler extends AbstractCheckHandler<Proto.CheckZCardRequest, Proto.CheckZCardResponse> {

    public CheckZCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckZCardResponse doCheckHandle(Proto.CheckZCardRequest req) throws Exception {
        int actual = getGlobalContext().getStorage().zCard(req.getKey());
        Proto.CheckZCardResponse.Builder builder = Proto.CheckZCardResponse.newBuilder();
        builder.setPass(Objects.equals(req.getCount(), actual));
        return builder.setActualCount(actual).build();
    }
}
