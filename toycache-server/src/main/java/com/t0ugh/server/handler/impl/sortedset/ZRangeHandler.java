package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.List;
import java.util.stream.Collectors;

@HandlerAnnotation(messageType = Proto.MessageType.ZRange, handlerType= HandlerType.Read)
public class ZRangeHandler extends AbstractGenericsHandler<Proto.ZRangeRequest, Proto.ZRangeResponse> {

    public ZRangeHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZRangeResponse doHandle(Proto.ZRangeRequest req) throws Exception {
        List<DBProto.ComparableString> l = getGlobalContext().getStorage().zRange(req.getKey(), req.getStart(), req.getEnd())
                .stream()
                .map(MemoryComparableString::toComparableString)
                .collect(Collectors.toList());
        return Proto.ZRangeResponse.newBuilder().addAllValues(l).build();
    }
}
