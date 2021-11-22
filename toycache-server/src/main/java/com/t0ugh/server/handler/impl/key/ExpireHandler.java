package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.Expire, handlerType= HandlerType.Write)
public class ExpireHandler extends AbstractGenericsHandler<Proto.ExpireRequest, Proto.ExpireResponse> {

    public ExpireHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ExpireResponse doHandle(Proto.ExpireRequest eRequest) throws Exception {
        boolean exists = getGlobalContext().getStorage().exists(eRequest.getKey());
        // 不存在直接返回
        if (!exists) {
            return Proto.ExpireResponse.newBuilder().setOk(false).build();
        }
        // 校验参数
        long expireTime = eRequest.getExpireTime();
        if (expireTime <= 0) {
            throw new InvalidParamException();
        }
        //实际的set
        getGlobalContext().getStorage().setExpire(eRequest.getKey(), eRequest.getExpireTime());
        return Proto.ExpireResponse.newBuilder().setOk(true).build();
    }
}
