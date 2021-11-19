package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.ZCard)
public class ZCardHandler extends AbstractHandler<Proto.ZCardRequest, Proto.ZCardResponse> {

    public ZCardHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZCardResponse doHandle(Proto.ZCardRequest zCardRequest) throws Exception {
        int count = getGlobalContext().getStorage().zCard(zCardRequest.getKey());
        return Proto.ZCardResponse.newBuilder().setCount(count).build();
    }
}
