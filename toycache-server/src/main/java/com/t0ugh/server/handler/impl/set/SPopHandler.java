package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

import java.lang.reflect.Field;
import java.util.Set;

@HandlerAnnotation(messageType = Proto.MessageType.SPop, handlerType= HandlerType.Write)
public class SPopHandler extends AbstractGenericsHandler<Proto.SPopRequest, Proto.SPopResponse> {

    public SPopHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SPopResponse doHandle(Proto.SPopRequest req) throws Exception {
        MessageUtils.assertIntNotNegative(req.getCount());
        Set<String> setValue = getGlobalContext().getStorage().sPop(req.getKey(), req.getCount());
        Field setField = req.getClass().getDeclaredField("setValue_");
        setField.setAccessible(true);
        setField.set(req, Lists.newArrayList(setValue));
        Field unRandField = req.getClass().getDeclaredField("unRand_");
        unRandField.setAccessible(true);
        unRandField.set(req, true);
        return Proto.SPopResponse.newBuilder().addAllSetValue(setValue).build();
    }
}
