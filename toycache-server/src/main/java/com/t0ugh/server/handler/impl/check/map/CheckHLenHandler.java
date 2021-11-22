package com.t0ugh.server.handler.impl.check.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.CheckHLen, handlerType= HandlerType.Check)
public class CheckHLenHandler extends AbstractCheckHandler<Proto.CheckHLenRequest, Proto.CheckHLenResponse> {

    public CheckHLenHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckHLenResponse doCheckHandle(Proto.CheckHLenRequest req) throws Exception {
        int actualLen = getGlobalContext().getStorage().hLen(req.getKey());
        Proto.CheckHLenResponse.Builder builder = Proto.CheckHLenResponse.newBuilder();
        builder.setPass(Objects.equals(req.getLen(), actualLen));
        return builder.setActualLen(actualLen).build();
    }
}
