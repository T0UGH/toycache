package com.t0ugh.server.handler.impl.string;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.Storage;

@HandlerAnnotation(type = Proto.MessageType.Set)
public class SetHandler extends AbstractHandler {

    public SetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.SetRequest sRequest = request.getSetRequest();
        getGlobalContext().getStorage().set(sRequest.getKey(), sRequest.getValue());
        responseBuilder.setSetResponse(Proto.SetResponse.newBuilder());
    }

}
