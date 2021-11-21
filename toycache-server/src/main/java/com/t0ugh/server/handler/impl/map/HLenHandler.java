package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(messageType = Proto.MessageType.HLen, handlerType= HandlerType.Read)
public class HLenHandler extends AbstractHandler<Proto.HLenRequest, Proto.HLenResponse> {

    public HLenHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HLenResponse doHandle(Proto.HLenRequest req) throws Exception {
        int len = getGlobalContext().getStorage().hLen(req.getKey());
        return Proto.HLenResponse.newBuilder().setLen(len).build();
    }
}
