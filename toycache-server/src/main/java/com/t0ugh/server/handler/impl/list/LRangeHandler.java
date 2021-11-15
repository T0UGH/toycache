package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.List;

@HandlerAnnotation(type = Proto.MessageType.LRange, isWrite = true)
public class LRangeHandler extends AbstractHandler {

    public LRangeHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.LRangeRequest req = request.getLRangeRequest();
        List<String> range = getGlobalContext().getStorage().lRange(req.getKey(), req.getStart(), req.getEnd());
        responseBuilder.setLRangeResponse(Proto.LRangeResponse.newBuilder().addAllValues(range));
    }
}
