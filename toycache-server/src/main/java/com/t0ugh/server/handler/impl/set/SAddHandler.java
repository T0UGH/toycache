package com.t0ugh.server.handler.impl.set;


import com.google.common.collect.Sets;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Set;


@HandlerAnnotation(type = Proto.MessageType.SAdd, isWrite = true)
public class SAddHandler extends AbstractHandler {

    public SAddHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws ValueTypeNotMatchException {
        Proto.SAddRequest sRequest = request.getSAddRequest();
        // 类型转换, 这一步会删掉重复的元素
        Set<String> value = Sets.newHashSet(sRequest.getSetValueList());
        int added = getGlobalContext().getStorage().sAdd(sRequest.getKey(), value);
        responseBuilder.setSAddResponse(Proto.SAddResponse.newBuilder().setAdded(added));
    }
}
