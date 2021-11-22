package com.t0ugh.server.handler.impl.check.list;

import com.google.common.base.Strings;
import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Objects;
import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.CheckLIndex, handlerType= HandlerType.Check)
public class CheckLIndexHandler extends AbstractCheckHandler<Proto.CheckLIndexRequest, Proto.CheckLIndexResponse> {

    public CheckLIndexHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckLIndexResponse doCheckHandle(Proto.CheckLIndexRequest checkLIndexRequest) throws Exception {
        Optional<String> op = getGlobalContext().getStorage().lIndex(checkLIndexRequest.getKey(), checkLIndexRequest.getIndex());
        if(!op.isPresent()){
            if(!Strings.isNullOrEmpty(checkLIndexRequest.getValue())){
                return Proto.CheckLIndexResponse.newBuilder().setPass(false).build();
            }
            return Proto.CheckLIndexResponse.newBuilder().setPass(true).build();
        }
        if(!Objects.equals(op.get(), checkLIndexRequest.getValue())){
            return Proto.CheckLIndexResponse.newBuilder().setPass(false).setActualValue(op.get()).build();

        }
        return Proto.CheckLIndexResponse.newBuilder().setPass(true).setActualValue(op.get()).build();
    }
}
