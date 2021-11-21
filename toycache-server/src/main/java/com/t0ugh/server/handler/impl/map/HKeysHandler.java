package com.t0ugh.server.handler.impl.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.HKeys, handlerType= HandlerType.Read)
public class HKeysHandler extends AbstractHandler<Proto.HKeysRequest, Proto.HKeysResponse> {

    public HKeysHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HKeysResponse doHandle(Proto.HKeysRequest req) throws Exception {
        Set<String> fields = getGlobalContext().getStorage().hKeys(req.getKey());
        return Proto.HKeysResponse.newBuilder().addAllFields(fields).build();
    }
}
