package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Set;

@HandlerAnnotation(type = Proto.MessageType.HKeys)
public class HKeysHandler extends AbstractHandler {

    public HKeysHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.HKeysRequest req = request.getHKeysRequest();
        Set<String> fields = getGlobalContext().getStorage().hKeys(req.getKey());
        responseBuilder.setHKeysResponse(Proto.HKeysResponse.newBuilder().addAllFields(fields));
    }
}
