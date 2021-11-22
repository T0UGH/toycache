package com.t0ugh.server.handler.impl.sort;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.ZCard, handlerType= HandlerType.Read)
public class ZCardHandler extends AbstractGenericsHandler<Proto.ZCardRequest, Proto.ZCardResponse> {

    public ZCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZCardResponse doHandle(Proto.ZCardRequest zCardRequest) throws Exception {
        int count = getGlobalContext().getStorage().zCard(zCardRequest.getKey());
        return Proto.ZCardResponse.newBuilder().setCount(count).build();
    }
}
