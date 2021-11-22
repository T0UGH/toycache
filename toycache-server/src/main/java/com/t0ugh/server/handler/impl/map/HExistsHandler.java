package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(messageType = Proto.MessageType.HExists, handlerType= HandlerType.Read)
public class HExistsHandler extends AbstractGenericsHandler<Proto.HExistsRequest, Proto.HExistsResponse> {

    public HExistsHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HExistsResponse doHandle(Proto.HExistsRequest req) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        boolean exists = getGlobalContext().getStorage().hExists(req.getKey(), req.getField());
        return Proto.HExistsResponse.newBuilder().setOk(exists).build();
    }
}
