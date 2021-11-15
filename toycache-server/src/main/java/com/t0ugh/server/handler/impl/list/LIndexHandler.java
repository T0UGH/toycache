package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LIndex)
public class LIndexHandler extends AbstractHandler {

    public LIndexHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.LIndexRequest req = request.getLIndexRequest();
        Proto.LIndexResponse.Builder builder = Proto.LIndexResponse.newBuilder();
        getGlobalContext().getStorage().lIndex(req.getKey(), req.getIndex()).ifPresent(builder::setValue);
        responseBuilder.setLIndexResponse(builder);
    }
}
