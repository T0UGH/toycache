package com.t0ugh.server.handler.impl.sortedset;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Optional;

@HandlerAnnotation(type = Proto.MessageType.ZRank)
public class ZRankHandler extends AbstractHandler {

    public ZRankHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ZRankRequest req = request.getZRankRequest();
        MessageUtils.assertStringNotNullOrEmpty(req.getMember());
        Optional<Integer> op = getGlobalContext().getStorage().zRank(req.getKey(), req.getMember());
        if(op.isPresent()){
            responseBuilder.setZRankResponse(Proto.ZRankResponse.newBuilder().setExists(true).setRank(op.get()).build());
        } else {
            responseBuilder.setZRankResponse(Proto.ZRankResponse.newBuilder().setExists(false).build());
        }
    }
}
