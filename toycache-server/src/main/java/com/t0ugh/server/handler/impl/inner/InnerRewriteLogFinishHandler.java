package com.t0ugh.server.handler.impl.inner;


import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.InnerRewriteLogFinish, checkExpire = false)
public class InnerRewriteLogFinishHandler extends AbstractHandler<Proto.InnerRewriteLogFinishRequest, Proto.InnerRewriteLogFinishResponse> {
    public InnerRewriteLogFinishHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.InnerRewriteLogFinishResponse doHandle(Proto.InnerRewriteLogFinishRequest unused) throws Exception {
        getGlobalContext().getGlobalState().setRewriteLogState(RewriteLogState.Normal);
        return Proto.InnerRewriteLogFinishResponse.newBuilder().setOk(true).build();
    }
}
