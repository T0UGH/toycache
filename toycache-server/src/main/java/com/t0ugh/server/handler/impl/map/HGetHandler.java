package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(type = Proto.MessageType.HGet)
public class HGetHandler extends AbstractHandler {

    public HGetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.HGetRequest req = request.getHGetRequest();
        Proto.HGetResponse.Builder builder = Proto.HGetResponse.newBuilder();
        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        getGlobalContext().getStorage().hGet(req.getKey(), req.getField()).ifPresent(builder::setValue);
        responseBuilder.setHGetResponse(builder);
    }
}
