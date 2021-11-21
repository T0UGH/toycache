package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(messageType = Proto.MessageType.Exists, handlerType= HandlerType.Read)
public class ExistsHandler extends AbstractHandler<Proto.ExistsRequest, Proto.ExistsResponse> {

    public ExistsHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ExistsResponse doHandle(Proto.ExistsRequest existsRequest) throws Exception {
        String key = existsRequest.getKey();
        boolean exists = getGlobalContext().getStorage().exists(key);
        return Proto.ExistsResponse.newBuilder().setExists(exists).build();
    }
}
