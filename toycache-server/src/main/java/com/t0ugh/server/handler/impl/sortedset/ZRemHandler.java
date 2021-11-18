package com.t0ugh.server.handler.impl.sortedset;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.stream.Collectors;

@HandlerAnnotation(type = Proto.MessageType.ZRem, isWrite = true)
public class ZRemHandler extends AbstractHandler {

    public ZRemHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ZRemRequest req = request.getZRemRequest();
        int deleted = getGlobalContext().getStorage().zRem(req.getKey(), req.getMembersList().stream().filter(v -> !Strings.isNullOrEmpty(v)).collect(Collectors.toSet()));
        responseBuilder.setZRemResponse(Proto.ZRemResponse.newBuilder().setDeleted(deleted));
    }
}
