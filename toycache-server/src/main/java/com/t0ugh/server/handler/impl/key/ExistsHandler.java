package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.Storage;

@HandlerAnnotation(type = Proto.MessageType.Exists)
public class ExistsHandler extends AbstractHandler {

    public ExistsHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ExistsRequest existsRequest = request.getExistsRequest();
        boolean exists = getGlobalContext().getStorage().exists(existsRequest.getKey());
        responseBuilder.setExistsResponse(Proto.ExistsResponse.newBuilder().setExists(exists));
    }

}