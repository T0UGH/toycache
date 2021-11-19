package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(type = Proto.MessageType.SRem, isWrite = true)
public class SRemHandler extends AbstractHandler<Proto.SRemRequest, Proto.SRemResponse> {

    public SRemHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SRemResponse doHandle(Proto.SRemRequest req) throws Exception {
        MessageUtils.assertCollectionNotEmpty(req.getMembersList());
        int deleted = getGlobalContext().getStorage().sRem(req.getKey(), Sets.newHashSet(req.getMembersList()));
        return Proto.SRemResponse.newBuilder().setDeleted(deleted).build();
    }
}
