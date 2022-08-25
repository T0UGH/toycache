package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

@HandlerAnnotation(messageType = Proto.MessageType.Noop, checkExpire = false, handlerType= HandlerType.Write)
public class NoopHandler extends AbstractGenericsHandler<Proto.NoopRequest, Proto.NoopResponse> {

    public NoopHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.NoopResponse doHandle(Proto.NoopRequest noopRequest) throws Exception {
        return Proto.NoopResponse.newBuilder().build();
    }
}
