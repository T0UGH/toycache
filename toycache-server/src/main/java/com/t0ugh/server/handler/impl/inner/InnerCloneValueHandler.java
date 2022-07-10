package com.t0ugh.server.handler.impl.inner;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.InnerCloneValue, checkExpire = false, handlerType= HandlerType.Other)
public class InnerCloneValueHandler extends AbstractGenericsHandler<Proto.InnerCloneValueRequest, Proto.InnerCloneValueResponse> {

    public InnerCloneValueHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.InnerCloneValueResponse doHandle(Proto.InnerCloneValueRequest innerCloneValueRequest) throws Exception {
        String key = innerCloneValueRequest.getKey();
        Proto.InnerCloneValueResponse.Builder builder = Proto.InnerCloneValueResponse.newBuilder();
        Optional<DBProto.KeyValue> optionalKeyValue = getGlobalContext().getStorage().cloneValue(key);
        optionalKeyValue.ifPresent(builder::setKeyValue);
        return builder.build();
    }
}
