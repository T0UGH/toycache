package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.InnerClearExpire, checkExpire = false, handlerType= HandlerType.Other)
public class InnerClearExpireHandler extends AbstractGenericsHandler<Proto.InnerClearExpireRequest, Proto.InnerClearExpireResponse> {

    public InnerClearExpireHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.InnerClearExpireResponse doHandle(Proto.InnerClearExpireRequest unused) throws Exception {
        int threshold = getGlobalContext().getConfig().getUpperKeyLimitOfPeriodicalDelete();
        int count = getGlobalContext().getStorage().delExpires(threshold);
        return Proto.InnerClearExpireResponse.newBuilder().setCleared(count).build();
    }
}
