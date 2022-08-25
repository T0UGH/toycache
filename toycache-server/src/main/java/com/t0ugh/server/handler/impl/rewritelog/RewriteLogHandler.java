package com.t0ugh.server.handler.impl.rewritelog;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.StateUtils;

import java.util.List;
import java.util.Map;

@HandlerAnnotation(messageType = Proto.MessageType.RewriteLog, checkExpire = false, handlerType= HandlerType.Other)
public class RewriteLogHandler extends AbstractGenericsHandler<Proto.RewriteLogRequest, Proto.RewriteLogResponse> {

    public RewriteLogHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.RewriteLogResponse doHandle(Proto.RewriteLogRequest unused) throws Exception {
        //当前不能处于rewriteLog状态也不能处于save状态
        if(StateUtils.isNowSaveOrRewriteLog(getGlobalContext())){
            return Proto.RewriteLogResponse.newBuilder().setOk(false).build();
        }
        getGlobalContext().getRewriteLogBuffer().clear();
        List<String> keys = getGlobalContext().getStorage().keys();
        Map<String, Long> expires = getGlobalContext().getStorage().getExpireMap();
        Proto.Request request = Proto.Request.newBuilder()
                 .setMessageType(Proto.MessageType.InnerRewriteLogSendKeyList)
                 .setInnerRewriteLogSendKeyListRequest(Proto.InnerRewriteLogSendKeyListRequest.newBuilder()
                        .addAllKeys(keys)
                        .putAllExpire(expires)
                        .build())
                .build();
        // 设置状态
        getGlobalContext().getGlobalState().setRewriteLogState(RewriteLogState.RewritingKeys);
        // 发送给WriteLogExecutor
        getGlobalContext().getWriteLogExecutor().submit(request);
        return Proto.RewriteLogResponse.newBuilder().setOk(true).build();
    }
}
