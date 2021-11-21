package com.t0ugh.server.handler.impl.string;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(messageType = Proto.MessageType.Get, handlerType= HandlerType.Read)
public class GetHandler extends AbstractHandler<Proto.GetRequest, Proto.GetResponse> {

    public GetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.GetResponse doHandle(Proto.GetRequest getRequest) throws Exception {
        Proto.GetResponse.Builder getResponseBuilder = Proto.GetResponse.newBuilder();
        getGlobalContext().getStorage().get(getRequest.getKey()).ifPresent(getResponseBuilder::setValue);
        return getResponseBuilder.build();
    }
}