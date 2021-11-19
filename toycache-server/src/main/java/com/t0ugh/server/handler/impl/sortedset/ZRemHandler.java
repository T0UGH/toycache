package com.t0ugh.server.handler.impl.sortedset;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.stream.Collectors;

@HandlerAnnotation(type = Proto.MessageType.ZRem, isWrite = true)
public class ZRemHandler extends AbstractHandler<Proto.ZRemRequest, Proto.ZRemResponse> {

    public ZRemHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZRemResponse doHandle(Proto.ZRemRequest req) throws Exception {
        int deleted = getGlobalContext().getStorage().zRem(req.getKey(), req.getMembersList().stream().filter(v -> !Strings.isNullOrEmpty(v)).collect(Collectors.toSet()));
        return Proto.ZRemResponse.newBuilder().setDeleted(deleted).build();
    }
}
