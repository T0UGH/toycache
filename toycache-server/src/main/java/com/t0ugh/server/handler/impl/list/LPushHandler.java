package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(type = Proto.MessageType.LPush, isWrite = true)
public class LPushHandler extends AbstractHandler<Proto.LPushRequest, Proto.LPushResponse> {

    public LPushHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LPushResponse doHandle(Proto.LPushRequest req) throws Exception {
        MessageUtils.assertCollectionNotEmpty(req.getValueList());
        int size = getGlobalContext().getStorage().lPush(req.getKey(), req.getValueList());
        return Proto.LPushResponse.newBuilder().setSize(size).build();
    }
}
