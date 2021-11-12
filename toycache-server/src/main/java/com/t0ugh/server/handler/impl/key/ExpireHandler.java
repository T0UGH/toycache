package com.t0ugh.server.handler.impl.key;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.Expire, isWrite = true)
public class ExpireHandler extends AbstractHandler {

    public ExpireHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws InvalidParamException {
        // 先判断Key是否存在
        Proto.ExpireRequest eRequest = request.getExpireRequest();
        boolean exists = getGlobalContext().getStorage().exists(eRequest.getKey());
        // 不存在直接返回
        if (!exists) {
            responseBuilder.setExpireResponse(Proto.ExpireResponse.newBuilder().setOk(false).build());
            return;
        }
        // 校验参数
        long expireTime = eRequest.getExpireTime();
        if (expireTime <= 0) {
            throw new InvalidParamException();
        }
        //实际的set
        getGlobalContext().getExpireMap().set(eRequest.getKey(), eRequest.getExpireTime());
        responseBuilder.setExpireResponse(Proto.ExpireResponse.newBuilder().setOk(true).build());
    }
}
