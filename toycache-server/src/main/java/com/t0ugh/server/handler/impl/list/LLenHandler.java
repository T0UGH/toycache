package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.LLen, handlerType= HandlerType.Read)
public class LLenHandler extends AbstractGenericsHandler<Proto.LLenRequest, Proto.LLenResponse> {

    public LLenHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LLenResponse doHandle(Proto.LLenRequest lLenRequest) throws Exception {
        int len = getGlobalContext().getStorage().lLen(lLenRequest.getKey());
        return Proto.LLenResponse.newBuilder().setCount(len).build();
    }
}
