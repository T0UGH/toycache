package com.t0ugh.server.handler.impl.string;

import com.google.common.base.Strings;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.Storage;

import java.util.Objects;

@HandlerAnnotation(type = Proto.MessageType.Get)
public class GetHandler extends AbstractHandler {

    public GetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws ValueTypeNotMatchException {
        Proto.GetRequest getRequest = request.getGetRequest();
        Proto.GetResponse.Builder getResponseBuilder = Proto.GetResponse.newBuilder();
        String value = getGlobalContext().getStorage().get(getRequest.getKey());
        if(!Strings.isNullOrEmpty(value)) {
            getResponseBuilder.setValue(value);
        }
        responseBuilder.setGetResponse(getResponseBuilder);
    }
}