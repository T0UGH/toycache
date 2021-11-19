package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.List;

@HandlerAnnotation(type = Proto.MessageType.LRange, isWrite = true)
public class LRangeHandler extends AbstractHandler<Proto.LRangeRequest, Proto.LRangeResponse> {

    public LRangeHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LRangeResponse doHandle(Proto.LRangeRequest req) throws Exception {
        List<String> range = getGlobalContext().getStorage().lRange(req.getKey(), req.getStart(), req.getEnd());
        return Proto.LRangeResponse.newBuilder().addAllValues(range).build();
    }
}
