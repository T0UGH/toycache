package com.t0ugh.server.handler.impl.check.string;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;
import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.CheckGet, handlerType= HandlerType.Check)
public class CheckGetHandler extends AbstractCheckHandler<Proto.CheckGetRequest, Proto.CheckGetResponse> {

    public CheckGetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckGetResponse doCheckHandle(Proto.CheckGetRequest checkGetRequest) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(checkGetRequest.getValue());
        Optional<String> op = getGlobalContext().getStorage().get(checkGetRequest.getKey());
        if(!op.isPresent()){
            return Proto.CheckGetResponse.newBuilder().setPass(false).build();
        }
        if(!Objects.equals(checkGetRequest.getValue(), op.get())){
            return Proto.CheckGetResponse.newBuilder().setPass(false).setActualValue(op.get()).build();
        }
        return Proto.CheckGetResponse.newBuilder().setPass(true).setActualValue(op.get()).build();
    }
}
