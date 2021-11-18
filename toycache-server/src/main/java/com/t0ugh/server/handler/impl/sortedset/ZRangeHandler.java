package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Collectors;

@HandlerAnnotation(type = Proto.MessageType.ZRange)
public class ZRangeHandler extends AbstractHandler {

    public ZRangeHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ZRangeRequest req = request.getZRangeRequest();
        List<DBProto.ComparableString> l = getGlobalContext().getStorage().zRange(req.getKey(), req.getStart(), req.getEnd())
                .stream()
                .map(MemoryComparableString::toComparableString)
                .collect(Collectors.toList());
        responseBuilder.setZRangeResponse(Proto.ZRangeResponse.newBuilder().addAllValues(l));
    }
}
