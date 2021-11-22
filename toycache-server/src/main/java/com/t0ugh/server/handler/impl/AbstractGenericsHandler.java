package com.t0ugh.server.handler.impl;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.MessageUtils;
import lombok.Getter;

public abstract class AbstractGenericsHandler<Req, Res> extends AbstractHandler {

    @Getter
    private Proto.MessageType messageType;

    public AbstractGenericsHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        this.messageType = request.getMessageType();
        String messageTypeStr = MessageUtils.getMessageTypeCamelString(request.getMessageType());
        @SuppressWarnings("unchecked")
        Req req = (Req) request.getField(request.getDescriptorForType().findFieldByName(messageTypeStr + "Request"));
        Res res = doHandle(req);
        responseBuilder.setField(responseBuilder.getDescriptorForType().findFieldByName(messageTypeStr + "Response"), res);
    }

    protected abstract Res doHandle(Req req) throws Exception ;
}
