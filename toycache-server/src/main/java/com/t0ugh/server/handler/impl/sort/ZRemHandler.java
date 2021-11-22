package com.t0ugh.server.handler.impl.sort;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

import java.util.stream.Collectors;

@HandlerAnnotation(messageType = Proto.MessageType.ZRem, handlerType= HandlerType.Write)
public class ZRemHandler extends AbstractGenericsHandler<Proto.ZRemRequest, Proto.ZRemResponse> {

    public ZRemHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZRemResponse doHandle(Proto.ZRemRequest req) throws Exception {
        int deleted = getGlobalContext().getStorage().zRem(req.getKey(), req.getMembersList().stream().filter(v -> !Strings.isNullOrEmpty(v)).collect(Collectors.toSet()));
        return Proto.ZRemResponse.newBuilder().setDeleted(deleted).build();
    }
}
