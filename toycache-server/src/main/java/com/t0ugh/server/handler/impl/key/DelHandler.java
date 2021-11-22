package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.Del, handlerType= HandlerType.Write)
public class DelHandler extends AbstractGenericsHandler<Proto.DelRequest, Proto.DelResponse> {

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
