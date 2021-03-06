package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(messageType = Proto.MessageType.HGet, handlerType= HandlerType.Read)
public class HGetHandler extends AbstractGenericsHandler<Proto.HGetRequest, Proto.HGetResponse> {

    public HGetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HGetResponse doHandle(Proto.HGetRequest req) throws Exception {
        Proto.HGetResponse.Builder builder = Proto.HGetResponse.newBuilder();
        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        getGlobalContext().getStorage().hGet(req.getKey(), req.getField()).ifPresent(builder::setValue);
        return builder.build();
    }
}
