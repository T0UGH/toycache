package com.t0ugh.server.handler.impl.check.list;

import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

@HandlerAnnotation(messageType = Proto.MessageType.CheckLLen, handlerType= HandlerType.Check)
public class CheckLLenHandler extends AbstractCheckHandler<Proto.CheckLLenRequest, Proto.CheckLLenResponse> {

    public CheckLLenHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckLLenResponse doCheckHandle(Proto.CheckLLenRequest req) throws Exception {
        MessageUtils.assertIntNotNegative(req.getCount());
        int count = getGlobalContext().getStorage().lLen(req.getKey());
        if(count != req.getCount()){
            return Proto.CheckLLenResponse.newBuilder().setPass(false).setActualCount(count).build();
        }
        return Proto.CheckLLenResponse.newBuilder().setPass(true).setActualCount(count).build();
    }
}
