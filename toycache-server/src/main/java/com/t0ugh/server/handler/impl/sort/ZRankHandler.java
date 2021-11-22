package com.t0ugh.server.handler.impl.sort;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.ZRank, handlerType= HandlerType.Read)
public class ZRankHandler extends AbstractGenericsHandler<Proto.ZRankRequest, Proto.ZRankResponse> {

    public ZRankHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZRankResponse doHandle(Proto.ZRankRequest req) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(req.getMember());
        Optional<Integer> op = getGlobalContext().getStorage().zRank(req.getKey(), req.getMember());
        if(op.isPresent()){
            return Proto.ZRankResponse.newBuilder().setExists(true).setRank(op.get()).build();
        } else {
            return Proto.ZRankResponse.newBuilder().setExists(false).build();
        }
    }
}
