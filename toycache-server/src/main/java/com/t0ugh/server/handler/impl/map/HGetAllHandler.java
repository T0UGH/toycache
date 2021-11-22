package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

import java.util.Map;

@HandlerAnnotation(messageType = Proto.MessageType.HGetAll, handlerType= HandlerType.Read)
public class HGetAllHandler extends AbstractGenericsHandler<Proto.HGetAllRequest, Proto.HGetAllResponse> {

    public HGetAllHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HGetAllResponse doHandle(Proto.HGetAllRequest req) throws Exception {
        Map<String, String> kvs = getGlobalContext().getStorage().hGetAll(req.getKey());
        return Proto.HGetAllResponse.newBuilder().putAllKvs(kvs).build();
    }
}
