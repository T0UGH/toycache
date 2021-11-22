package com.t0ugh.server.handler.impl.map;


import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(messageType = Proto.MessageType.HSet, handlerType= HandlerType.Write)
public class HSetHandler extends AbstractGenericsHandler<Proto.HSetRequest, Proto.HSetResponse> {

    public HSetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HSetResponse doHandle(Proto.HSetRequest req) throws Exception {

        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        getGlobalContext().getStorage().hSet(req.getKey(), req.getField(), req.getValue());
        return Proto.HSetResponse.newBuilder().setOk(true).build();
    }
}
