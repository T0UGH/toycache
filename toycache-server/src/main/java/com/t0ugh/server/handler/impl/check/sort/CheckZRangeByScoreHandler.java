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

@HandlerAnnotation(messageType = Proto.MessageType.ZRangeByScore, handlerType= HandlerType.Check)
public class CheckZRangeByScoreHandler extends AbstractCheckHandler<Proto.CheckZRangeByScoreRequest, Proto.CheckZRangeByScoreResponse> {

    public CheckZRangeByScoreHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckZRangeByScoreResponse doCheckHandle(Proto.CheckZRangeByScoreRequest req) throws Exception {
        List<DBProto.ComparableString> actual = getGlobalContext().getStorage()
                .zRangeByScore(req.getKey(), req.getMin(), req.getMax())
                .stream().map(MemoryComparableString::toComparableString).collect(Collectors.toList());
        Proto.CheckZRangeByScoreResponse.Builder builder = Proto.CheckZRangeByScoreResponse.newBuilder();
        builder.setPass(Objects.equals(req.getValuesList(), actual));
        return builder.addAllActualValues(actual).build();
    }
}
