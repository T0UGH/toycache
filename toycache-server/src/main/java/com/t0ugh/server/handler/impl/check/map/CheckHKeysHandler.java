package com.t0ugh.server.handler.impl.check.map;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;

import java.util.Objects;
import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.CheckHKeys, handlerType= HandlerType.Check)
public class CheckHKeysHandler extends AbstractCheckHandler<Proto.CheckHKeysRequest, Proto.CheckHKeysResponse> {

    public CheckHKeysHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckHKeysResponse doCheckHandle(Proto.CheckHKeysRequest req) throws Exception {
        Set<String> actualFields = getGlobalContext().getStorage().hKeys(req.getKey());
        Proto.CheckHKeysResponse.Builder builder = Proto.CheckHKeysResponse.newBuilder();
        builder.setPass(Objects.equals(actualFields, Sets.newHashSet(req.getFieldsList())));
        return builder.addAllActualFields(actualFields).build();
    }
}
