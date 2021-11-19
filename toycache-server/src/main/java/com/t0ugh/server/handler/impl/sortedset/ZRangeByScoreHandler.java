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
public class ZRangeByScoreHandler extends AbstractHandler<Proto.ZRangeByScoreRequest, Proto.ZRangeByScoreResponse> {

    public ZRangeByScoreHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZRangeByScoreResponse doHandle(Proto.ZRangeByScoreRequest req) throws Exception {
        List<DBProto.ComparableString> c = getGlobalContext().getStorage()
                .zRangeByScore(req.getKey(), req.getMin(), req.getMax())
                .stream().map(MemoryComparableString::toComparableString).collect(Collectors.toList());
        return Proto.ZRangeByScoreResponse.newBuilder().addAllValues(c).build();
    }
}
