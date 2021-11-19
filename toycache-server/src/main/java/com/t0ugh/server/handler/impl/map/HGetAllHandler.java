package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Map;

@HandlerAnnotation(type = Proto.MessageType.HGetAll)
public class HGetAllHandler extends AbstractHandler<Proto.HGetAllRequest, Proto.HGetAllResponse> {

    public HGetAllHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HGetAllResponse doHandle(Proto.HGetAllRequest req) throws Exception {
        Map<String, String> kvs = getGlobalContext().getStorage().hGetAll(req.getKey());
        return Proto.HGetAllResponse.newBuilder().putAllKvs(kvs).build();
    }
}
