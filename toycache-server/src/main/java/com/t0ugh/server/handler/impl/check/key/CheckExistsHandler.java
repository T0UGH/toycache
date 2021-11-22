package com.t0ugh.server.handler.impl.check.key;

import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.CheckExists, handlerType= HandlerType.Check)
public class CheckExistsHandler extends AbstractCheckHandler<Proto.CheckExistsRequest, Proto.CheckExistsResponse> {

    public CheckExistsHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckExistsResponse doCheckHandle(Proto.CheckExistsRequest checkExistsRequest) throws Exception {
        boolean actualExists = getGlobalContext().getStorage().exists(checkExistsRequest.getKey());
        if(!Objects.equals(checkExistsRequest.getExists(), actualExists)){
            return Proto.CheckExistsResponse.newBuilder().setPass(false).setActualExists(actualExists).build();
        }
        return Proto.CheckExistsResponse.newBuilder().setPass(true).setActualExists(actualExists).build();
    }
}
