package com.t0ugh.server.handler.impl.rewritelog;


import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

@HandlerAnnotation(messageType = Proto.MessageType.InnerRewriteLogFinish, checkExpire = false, handlerType= HandlerType.Other)
public class InnerRewriteLogFinishHandler extends AbstractGenericsHandler<Proto.InnerRewriteLogFinishRequest, Proto.InnerRewriteLogFinishResponse> {
    public InnerRewriteLogFinishHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.InnerRewriteLogFinishResponse doHandle(Proto.InnerRewriteLogFinishRequest unused) throws Exception {
        getGlobalContext().getGlobalState().setRewriteLogState(RewriteLogState.Normal);
        return Proto.InnerRewriteLogFinishResponse.newBuilder().setOk(true).build();
    }
}
