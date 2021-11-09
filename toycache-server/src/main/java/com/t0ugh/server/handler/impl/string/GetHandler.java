package com.t0ugh.server.handler.impl.string;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.Storage;

@HandlerAnnotation(type = Proto.MessageType.Get)
public class GetHandler extends AbstractHandler {

    public GetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws ValueTypeNotMatchException {
        Proto.GetRequest getRequest = request.getGetRequest();
        String value = getGlobalContext().getStorage().get(getRequest.getKey());
        responseBuilder.setGetResponse(Proto.GetResponse.newBuilder().setValue(value));
    }
}