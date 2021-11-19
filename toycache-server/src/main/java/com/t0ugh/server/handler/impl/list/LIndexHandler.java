package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LIndex)
public class LIndexHandler extends AbstractHandler<Proto.LIndexRequest, Proto.LIndexResponse> {

    public LIndexHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LIndexResponse doHandle(Proto.LIndexRequest req) throws Exception {
        Proto.LIndexResponse.Builder builder = Proto.LIndexResponse.newBuilder();
        getGlobalContext().getStorage().lIndex(req.getKey(), req.getIndex()).ifPresent(builder::setValue);
        return builder.build();
    }
}
