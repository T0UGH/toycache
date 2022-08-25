package com.t0ugh.server.handler.impl.rewritelog;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

@HandlerAnnotation(messageType = Proto.MessageType.InnerRewriteLogNextKey, checkExpire = false, handlerType= HandlerType.Other)
public class InnerRewriteLogNextKeyHandler extends AbstractGenericsHandler<Proto.InnerRewriteLogNextKeyRequest, Proto.InnerRewriteLogNextKeyResponse>  {

    public InnerRewriteLogNextKeyHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.InnerRewriteLogNextKeyResponse doHandle(Proto.InnerRewriteLogNextKeyRequest innerRewriteLogNextKeyRequest) throws Exception {
        String key = innerRewriteLogNextKeyRequest.getKey();
        // 此时key是有可能不存在的,可能发生过期
        Proto.InnerRewriteLogSendOneKeyRequest.Builder builder = Proto.InnerRewriteLogSendOneKeyRequest.newBuilder();
        builder.setKey(key);
        if(!getGlobalContext().getStorage().exists(key)){
            builder.setExist(false);
        } else {
            builder.setValue(getGlobalContext().getStorage().backdoor().get(key).toValueObject());
        }
        //删掉这个key之前的buffer,因为在ValueObject上已经体现了
        getGlobalContext().getRewriteLogBuffer().delete(key);
        getGlobalContext().getWriteLogExecutor().submit(Proto.Request.newBuilder()
                        .setMessageType(Proto.MessageType.InnerRewriteLogSendOneKey)
                        .setInnerRewriteLogSendOneKeyRequest(builder)
                .build());
        return Proto.InnerRewriteLogNextKeyResponse.newBuilder().build();
    }
}
