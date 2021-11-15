package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(type = Proto.MessageType.LPush, isWrite = true)
public class LPushHandler extends AbstractHandler {

    public LPushHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.LPushRequest req = request.getLPushRequest();
        MessageUtils.assertCollectionNotEmpty(req.getValueList());
        int size = getGlobalContext().getStorage().lPush(req.getKey(), req.getValueList());
        responseBuilder.setLPushResponse(Proto.LPushResponse.newBuilder().setSize(size));
    }
}
