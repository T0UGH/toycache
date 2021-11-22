package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.ZCount, handlerType= HandlerType.Read)
public class ZCountHandler extends AbstractGenericsHandler<Proto.ZCountRequest, Proto.ZCountResponse> {

    public ZCountHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZCountResponse doHandle(Proto.ZCountRequest req) throws Exception {
        int count = getGlobalContext().getStorage().zCount(req.getKey(), req.getMin(), req.getMax());
        return Proto.ZCountResponse.newBuilder().setCount(count).build();
    }
}
