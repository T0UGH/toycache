package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LTrim, isWrite = true)
public class LTrimHandler extends AbstractHandler<Proto.LTrimRequest, Proto.LTrimResponse> {

    public LTrimHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LTrimResponse doHandle(Proto.LTrimRequest req) throws Exception {
        boolean ok = getGlobalContext().getStorage().lTrim(req.getKey(), req.getStart(), req.getEnd());
        return Proto.LTrimResponse.newBuilder().setOk(ok).build();
    }
}
