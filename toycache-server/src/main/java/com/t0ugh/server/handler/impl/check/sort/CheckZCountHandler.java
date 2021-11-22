package com.t0ugh.server.handler.impl.check.sort;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.ZCount, handlerType= HandlerType.Check)
public class CheckZCountHandler extends AbstractCheckHandler<Proto.CheckZCountRequest, Proto.CheckZCountResponse> {

    public CheckZCountHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckZCountResponse doCheckHandle(Proto.CheckZCountRequest req) throws Exception {
        int actual = getGlobalContext().getStorage().zCount(req.getKey(), req.getMin(), req.getMax());
        Proto.CheckZCountResponse.Builder builder = Proto.CheckZCountResponse.newBuilder();
        builder.setPass(Objects.equals(req.getCount(), actual));
        return builder.setActualCount(actual).build();
    }
}
