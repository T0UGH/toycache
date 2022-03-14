package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.RewriteLog, checkExpire = false, handlerType= HandlerType.Other)
public class RewriteLogHandler extends AbstractGenericsHandler<Proto.RewriteLogRequest, Proto.RewriteLogResponse> {

    public RewriteLogHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.RewriteLogResponse doHandle(Proto.RewriteLogRequest unused) throws Exception {
        if(Objects.equals(RewriteLogState.Rewriting, getGlobalContext().getGlobalState().getRewriteLogState())){
            return Proto.RewriteLogResponse.newBuilder().setOk(false).build();
        }
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerRewriteLog)
                .setInnerRewriteLogRequest(Proto.InnerRewriteLogRequest.newBuilder()
                        .setDb(getGlobalContext().getStorage().toUnModifiableDB(getGlobalContext().getGlobalState().getWriteCount().get())))
                .build();
        getGlobalContext().getGlobalState().setRewriteLogState(RewriteLogState.Rewriting);
        getGlobalContext().getWriteLogExecutor().submit(request);
        return Proto.RewriteLogResponse.newBuilder().setOk(true).build();
    }
}
