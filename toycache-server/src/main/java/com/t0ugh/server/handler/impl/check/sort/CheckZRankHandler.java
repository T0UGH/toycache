package com.t0ugh.server.handler.impl.check.sort;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;
import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.ZRank, handlerType= HandlerType.Check)
public class CheckZRankHandler extends AbstractCheckHandler<Proto.CheckZRankRequest, Proto.CheckZRankResponse> {

    public CheckZRankHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckZRankResponse doCheckHandle(Proto.CheckZRankRequest req) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(req.getMember());
        Optional<Integer> op = getGlobalContext().getStorage().zRank(req.getKey(), req.getMember());
        if(op.isPresent()){
            if (!req.getExists()){
                return Proto.CheckZRankResponse.newBuilder().setPass(false).setActualExists(true).build();
            }
            Proto.CheckZRankResponse.Builder builder = Proto.CheckZRankResponse.newBuilder();
            builder.setPass(Objects.equals(op.get(), req.getRank()));
            return builder.setActualExists(true).setActualRank(op.get()).build();
        } else {
            if (!req.getExists()){
                return Proto.CheckZRankResponse.newBuilder().setPass(true).setActualExists(false).build();
            }
            return Proto.CheckZRankResponse.newBuilder().setPass(false).setActualExists(false).build();
        }
    }
}
