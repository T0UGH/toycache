package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.SCard, handlerType= HandlerType.Read)
public class SCardHandler extends AbstractGenericsHandler<Proto.SCardRequest, Proto.SCardResponse> {

    public SCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SCardResponse doHandle(Proto.SCardRequest req) throws Exception {
        int size = getGlobalContext().getStorage().sCard(req.getKey());
        return Proto.SCardResponse.newBuilder().setSize(size).build();
    }
}
