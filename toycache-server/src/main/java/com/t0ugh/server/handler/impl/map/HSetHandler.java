package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(type = Proto.MessageType.HSet, isWrite = true)
public class HSetHandler extends AbstractHandler {

    public HSetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws ValueTypeNotMatchException, InvalidParamException {
        Proto.HSetRequest req = request.getHSetRequest();
        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        getGlobalContext().getStorage().hSet(req.getKey(), req.getField(), req.getValue());
        responseBuilder.setHSetResponse(Proto.HSetResponse.newBuilder().setOk(true));
    }
}
