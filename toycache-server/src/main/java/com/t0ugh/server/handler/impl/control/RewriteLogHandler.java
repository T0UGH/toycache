package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.Objects;

@HandlerAnnotation(type = Proto.MessageType.RewriteLog, checkExpire = false)
public class RewriteLogHandler extends AbstractHandler {

    public RewriteLogHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request unused, Proto.Response.Builder responseBuilder) throws Exception {
        if(Objects.equals(RewriteLogState.Rewriting, getGlobalContext().getGlobalState().getRewriteLogState())){
            responseBuilder.setRewriteLogResponse(Proto.RewriteLogResponse.newBuilder().setOk(false));
            return;
        }
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerRewriteLog)
                .setInnerRewriteLogRequest(Proto.InnerRewriteLogRequest.newBuilder()
                        .setDb(getGlobalContext().getStorage().toUnModifiableDB()))
                .build();
        getGlobalContext().getGlobalState().setRewriteLogState(RewriteLogState.Rewriting);
        getGlobalContext().getWriteLogExecutor().submit(request);
        responseBuilder.setRewriteLogResponse(Proto.RewriteLogResponse.newBuilder().setOk(true));
    }
}
