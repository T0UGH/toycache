package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Map;

@HandlerAnnotation(type = Proto.MessageType.HGetAll)
public class HGetAllHandler extends AbstractHandler {

    public HGetAllHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.HGetAllRequest req = request.getHGetAllRequest();
        Map<String, String> kvs = getGlobalContext().getStorage().hGetAll(req.getKey());
        responseBuilder.setHGetAllResponse(Proto.HGetAllResponse.newBuilder().putAllKvs(kvs));
    }
}
