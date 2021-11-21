package com.t0ugh.server.handler.impl.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.SPop, handlerType= HandlerType.Write)
public class SPopHandler extends AbstractHandler<Proto.SPopRequest, Proto.SPopResponse> {

    public SPopHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SPopResponse doHandle(Proto.SPopRequest req) throws Exception {
        MessageUtils.assertIntNotNegative(req.getCount());
        Set<String> setValue = getGlobalContext().getStorage().sPop(req.getKey(), req.getCount());
        return Proto.SPopResponse.newBuilder().addAllSetValue(setValue).build();
    }
}
