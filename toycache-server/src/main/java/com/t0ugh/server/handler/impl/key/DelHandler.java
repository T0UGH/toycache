package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.Del, isWrite = true)
public class DelHandler extends AbstractHandler<Proto.DelRequest, Proto.DelResponse> {

    public DelHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.DelResponse doHandle(Proto.DelRequest delRequest) throws Exception {
        String key = delRequest.getKey();
        boolean ok = getGlobalContext().getStorage().del(delRequest.getKey());
        // 在超时表中也删除这个键
        getGlobalContext().getStorage().delExpire(key);
        return Proto.DelResponse.newBuilder().setOk(ok).build();
    }
}
