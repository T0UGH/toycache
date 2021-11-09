package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.Del)
public class DelHandler extends AbstractHandler {

    public DelHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.DelRequest delRequest = request.getDelRequest();
        String key = delRequest.getKey();
        boolean ok = getGlobalContext().getStorage().del(delRequest.getKey());
        // 在超时表中也删除这个键
        getGlobalContext().getExpireMap().del(key);
        responseBuilder.setDelResponse(Proto.DelResponse.newBuilder().setOk(ok));
    }
}
