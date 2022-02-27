package com.t0ugh.server.handler.impl.string;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.Set, handlerType= HandlerType.Write)
public class SetHandler extends AbstractGenericsHandler<Proto.SetRequest, Proto.SetResponse> {

    private Optional<String> oldValue;

    public SetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SetResponse doHandle(Proto.SetRequest setRequest) throws Exception {
        oldValue = getGlobalContext().getStorage().get(setRequest.getKey());
        MessageUtils.assertStringNotNullOrEmpty(setRequest.getValue());
        getGlobalContext().getStorage().set(setRequest.getKey(), setRequest.getValue());
        return Proto.SetResponse.newBuilder().setOk(true).build();
    }

}
