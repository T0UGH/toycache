package com.t0ugh.server.handler.impl.map;


import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Set;

@HandlerAnnotation(type = Proto.MessageType.HDel, isWrite = true)
public class HDelHandler extends AbstractHandler<Proto.HDelRequest, Proto.HDelResponse> {

    public HDelHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HDelResponse doHandle(Proto.HDelRequest req) throws Exception {
        Set<String> fields = Sets.newHashSet(req.getFieldsList());
        MessageUtils.assertCollectionNotEmpty(fields);
        MessageUtils.assertAllStringNotNullOrEmpty(fields);
        int deleted = getGlobalContext().getStorage().hDel(req.getKey(), fields);
        return Proto.HDelResponse.newBuilder().setDeleted(deleted).build();
    }
}
