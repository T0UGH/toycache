package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.Storage;

import java.util.List;

@HandlerAnnotation(type = Proto.MessageType.InnerClearExpire, checkExpire = false)
public class InnerClearExpireHandler extends AbstractHandler {

    public InnerClearExpireHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        int limit = getGlobalContext().getConfig().getUpperKeyLimitOfPeriodicalDelete();
        List<String> keyNeedDelete = getGlobalContext().getExpireMap().deleteExpires(limit);
        Storage storage = getGlobalContext().getStorage();
        keyNeedDelete.forEach(storage::del);
        responseBuilder.setInnerClearExpireResponse(
                Proto.InnerClearExpireResponse.newBuilder().setCleared(keyNeedDelete.size()));
    }
}
