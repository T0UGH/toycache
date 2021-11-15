package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(type = Proto.MessageType.HExists, isWrite = true)
public class HExistsHandler extends AbstractHandler {

    public HExistsHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.HExistsRequest req = request.getHExistRequest();
        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        boolean exists = getGlobalContext().getStorage().hExists(req.getKey(), req.getField());
        responseBuilder.setHExistResponse(Proto.HExistsResponse.newBuilder().setOk(exists));
    }
}
