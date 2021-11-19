package com.t0ugh.server.handler.impl.string;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.Set, isWrite = true)
public class SetHandler extends AbstractHandler<Proto.SetRequest, Proto.SetResponse> {

    public SetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SetResponse doHandle(Proto.SetRequest setRequest) throws Exception {
        getGlobalContext().getStorage().set(setRequest.getKey(), setRequest.getValue());
        return Proto.SetResponse.newBuilder().setOk(true).build();
    }
}
