package com.t0ugh.server.handler.impl.string;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.Get)
public class GetHandler extends AbstractHandler<Proto.GetRequest, Proto.GetResponse> {

    public GetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.GetResponse doHandle(Proto.GetRequest getRequest) throws Exception {
        Proto.GetResponse.Builder getResponseBuilder = Proto.GetResponse.newBuilder();
        String value = getGlobalContext().getStorage().get(getRequest.getKey());
        if(!Strings.isNullOrEmpty(value)) {
            getResponseBuilder.setValue(value);
        }
        return getResponseBuilder.build();
    }
}