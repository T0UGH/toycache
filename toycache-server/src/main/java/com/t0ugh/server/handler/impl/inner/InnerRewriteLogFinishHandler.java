package com.t0ugh.server.handler.impl.inner;


import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(type = Proto.MessageType.InnerRewriteLogFinish, checkExpire = false)
public class InnerRewriteLogFinishHandler extends AbstractHandler {
    public InnerRewriteLogFinishHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request unused, Proto.Response.Builder responseBuilder) throws Exception {
        getGlobalContext().getGlobalState().setRewriteLogState(RewriteLogState.Normal);
        responseBuilder.setInnerRewriteLogFinishResponse(
                Proto.InnerRewriteLogFinishResponse.newBuilder().setOk(true));
    }
}
