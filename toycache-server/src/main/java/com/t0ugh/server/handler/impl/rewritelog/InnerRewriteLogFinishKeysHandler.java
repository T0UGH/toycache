package com.t0ugh.server.handler.impl.rewritelog;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

import java.util.List;

@HandlerAnnotation(messageType = Proto.MessageType.InnerRewriteLogFinishKeys, checkExpire = false, handlerType= HandlerType.Other)
public class InnerRewriteLogFinishKeysHandler extends AbstractGenericsHandler<Proto.InnerRewriteLogFinishKeysRequest, Proto.InnerRewriteLogFinishKeysResponse> {

    public InnerRewriteLogFinishKeysHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.InnerRewriteLogFinishKeysResponse doHandle(Proto.InnerRewriteLogFinishKeysRequest innerRewriteLogFinishKeysRequest) throws Exception {
        getGlobalContext().getGlobalState().setRewriteLogState(RewriteLogState.RewritingLogs);
        List<Proto.Request> requestList = getGlobalContext().getRewriteLogBuffer().toList();
        getGlobalContext().getRewriteLogBuffer().clear();
        getGlobalContext().getWriteLogExecutor().submit(Proto.Request.newBuilder()
                        .setMessageType(Proto.MessageType.InnerRewriteLogSendLogs)
                        .setInnerRewriteLogSendLogsRequest(Proto.InnerRewriteLogSendLogsRequest.newBuilder()
                                .addAllRequests(requestList)
                                .build())
                .build());
        return null;
    }
}
