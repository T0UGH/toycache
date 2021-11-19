package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.SCard)
public class SCardHandler extends AbstractHandler<Proto.SCardRequest, Proto.SCardResponse> {

    public SCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SCardResponse doHandle(Proto.SCardRequest req) throws Exception {
        int size = getGlobalContext().getStorage().sCard(req.getKey());
        return Proto.SCardResponse.newBuilder().setSize(size).build();
    }
}
