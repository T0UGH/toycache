package com.t0ugh.server.handler.impl.check.map;

import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.CheckHExists, handlerType= HandlerType.Check)
public class CheckHExistsHandler extends AbstractCheckHandler<Proto.CheckHExistsRequest, Proto.CheckHExistsResponse> {

    public CheckHExistsHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckHExistsResponse doCheckHandle(Proto.CheckHExistsRequest req) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        boolean actualExists = getGlobalContext().getStorage().hExists(req.getKey(), req.getField());
        if(!Objects.equals(req.getExists(), actualExists)){
            return Proto.CheckHExistsResponse.newBuilder().setPass(false).setActualExists(actualExists).build();
        }
        return Proto.CheckHExistsResponse.newBuilder().setPass(true).setActualExists(actualExists).build();
    }
}
