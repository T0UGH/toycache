package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.Storage;
import lombok.AllArgsConstructor;

@HandlerAnnotation(type = Proto.MessageType.Del)
public class DelHandler extends AbstractHandler {

    public DelHandler(Storage storage) {
        super(storage);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.DelRequest delRequest = request.getDelRequest();
        boolean ok = getStorage().del(delRequest.getKey());
        responseBuilder.setDelResponse(Proto.DelResponse.newBuilder().setOk(ok));
    }
}
