package com.t0ugh.server.handler.impl.check.string;

import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;
import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.CheckGet, handlerType= HandlerType.Check)
public class CheckGetHandler extends AbstractHandler<Proto.CheckGetRequest, Proto.CheckGetResponse> {

    public CheckGetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckGetResponse doHandle(Proto.CheckGetRequest checkGetRequest) throws Exception {
        Optional<String> op = getGlobalContext().getStorage().get(checkGetRequest.getKey());
        MessageUtils.assertStringNotNullOrEmpty(checkGetRequest.getValue());
        if(!op.isPresent()){
            throw new CheckNotPassException(Proto.MessageType.CheckGet,
                    Proto.CheckGetResponse.newBuilder().setPass(false).build());
        }
        if(!Objects.equals(checkGetRequest.getValue(), op.get())){
            throw new CheckNotPassException(Proto.MessageType.CheckGet,
                    Proto.CheckGetResponse.newBuilder().setPass(false).setActualValue(op.get()).build());
        }
        return Proto.CheckGetResponse.newBuilder().setPass(true).setActualValue(op.get()).build();
    }
}
