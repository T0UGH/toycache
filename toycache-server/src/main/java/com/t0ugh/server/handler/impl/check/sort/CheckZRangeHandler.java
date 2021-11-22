package com.t0ugh.server.handler.impl.check.sort;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@HandlerAnnotation(messageType = Proto.MessageType.ZRange, handlerType= HandlerType.Check)
public class CheckZRangeHandler extends AbstractCheckHandler<Proto.CheckZRangeRequest, Proto.CheckZRangeResponse> {

    public CheckZRangeHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckZRangeResponse doCheckHandle(Proto.CheckZRangeRequest req) throws Exception {
        List<DBProto.ComparableString> actual = getGlobalContext().getStorage().zRange(req.getKey(), req.getStart(), req.getEnd())
                .stream()
                .map(MemoryComparableString::toComparableString)
                .collect(Collectors.toList());
        Proto.CheckZRangeResponse.Builder builder = Proto.CheckZRangeResponse.newBuilder();
        builder.setPass(Objects.equals(req.getValuesList(), actual));
        return builder.addAllActualValues(actual).build();
    }
}
