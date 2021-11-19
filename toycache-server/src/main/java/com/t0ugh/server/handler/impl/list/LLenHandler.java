package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.LLen)
public class LLenHandler extends AbstractHandler<Proto.LLenRequest, Proto.LLenResponse> {

    public LLenHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LLenResponse doHandle(Proto.LLenRequest lLenRequest) throws Exception {
        int len = getGlobalContext().getStorage().lLen(lLenRequest.getKey());
        return Proto.LLenResponse.newBuilder().setCount(len).build();
    }
}
