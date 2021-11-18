package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.List;
import java.util.stream.Collectors;

@HandlerAnnotation(type = Proto.MessageType.ZRangeByScore)
public class ZRangeByScoreHandler extends AbstractHandler {

    public ZRangeByScoreHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ZRangeByScoreRequest req = request.getZRangeByScoreRequest();
        List<DBProto.ComparableString> c = getGlobalContext().getStorage()
                .zRangeByScore(req.getKey(), req.getMin(), req.getMax())
                .stream().map(MemoryComparableString::toComparableString).collect(Collectors.toList());
        responseBuilder.setZRangeByScoreResponse(Proto.ZRangeByScoreResponse.newBuilder().addAllValues(c));
    }
}
