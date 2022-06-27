package com.t0ugh.server.handler.impl.replicate;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.GlobalState;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.InnerMasterUpdateFollowerProcess, checkExpire = false, handlerType= HandlerType.Other)
public class InnerMasterUpdateFollowerProcessHandler extends AbstractGenericsHandler<Proto.InnerMasterUpdateFollowerProcessRequest, Proto.InnerMasterUpdateFollowerProcessResponse> {

    public InnerMasterUpdateFollowerProcessHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.InnerMasterUpdateFollowerProcessResponse doHandle(Proto.InnerMasterUpdateFollowerProcessRequest req) throws Exception {
        GlobalState state = getGlobalContext().getGlobalState();
        if (Objects.equals(state.getGroupId(), req.getGroupId())){
            // 直接更新, 从节点的进度有可能往前降
            state.getFollowerProcess().put(req.getServerId(),  req.getLastWriteId());
        }
        // 如果client里面没有就先创建一个创建client的任务
        if (!state.getFollowerClients().containsKey(req.getServerId())){
            getGlobalContext().getCreateToyCacheClientExecutor().submit(Proto.Request.newBuilder()
                            .setMessageType(Proto.MessageType.InnerCreateClient)
                            .setInnerCreateClientRequest(Proto.InnerCreateClientRequest.newBuilder()
                                    .setIp(req.getIp())
                                    .setPort(req.getPort())
                                    .setServerId(req.getServerId()).build())
                    .build());
        }
        return Proto.InnerMasterUpdateFollowerProcessResponse.newBuilder().build();
    }
}
