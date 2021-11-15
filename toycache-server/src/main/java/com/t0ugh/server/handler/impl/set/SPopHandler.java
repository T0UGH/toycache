package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Set;

@HandlerAnnotation(type = Proto.MessageType.SPop, isWrite = true)
public class SPopHandler extends AbstractHandler {

    public SPopHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.SPopRequest req = request.getSPopRequest();
        Set<String> setValue = getGlobalContext().getStorage().sPop(req.getKey(), req.getCount());
        responseBuilder.setSPopResponse(Proto.SPopResponse.newBuilder().addAllSetValue(setValue));
    }
}
