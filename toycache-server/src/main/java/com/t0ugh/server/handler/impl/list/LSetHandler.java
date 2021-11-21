package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(messageType = Proto.MessageType.LSet, handlerType= HandlerType.Write)
public class LSetHandler extends AbstractHandler<Proto.LSetRequest, Proto.LSetResponse> {

    public LSetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LSetResponse doHandle(Proto.LSetRequest req) throws Exception {
        boolean ok = getGlobalContext().getStorage().lSet(req.getKey(), req.getIndex(), req.getNewValue());
        return Proto.LSetResponse.newBuilder().setOk(ok).build();
    }
}
