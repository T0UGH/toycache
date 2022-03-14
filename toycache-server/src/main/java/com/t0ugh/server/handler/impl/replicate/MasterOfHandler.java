package com.t0ugh.server.handler.impl.replicate;

import com.google.common.collect.Lists;
import com.t0ugh.client.ToyCacheClient;
import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.GlobalState;
import com.t0ugh.server.callback.SyncSlaveResponseCallback;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

@HandlerAnnotation(messageType = Proto.MessageType.MasterOf, checkExpire = false, handlerType= HandlerType.Other)
public class MasterOfHandler extends AbstractGenericsHandler<Proto.MasterOfRequest, Proto.MasterOfResponse>  {

    public MasterOfHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.MasterOfResponse doHandle(Proto.MasterOfRequest masterOfRequest) throws Exception {
        GlobalState state = getGlobalContext().getGlobalState();
        ToyCacheClient toyCacheClient = new ToyCacheClient(masterOfRequest.getIp(), masterOfRequest.getPort());
        state.getSlavesClient().put(masterOfRequest.getServerId(), toyCacheClient);
        state.getSlavesProgress().put(masterOfRequest.getServerId(), -1L);
        Proto.Request toSlaveRequest = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Sync)
                .setSyncRequest(Proto.SyncRequest.newBuilder()
                        .setClusterId(state.getClusterId())
                        .setIp(getGlobalContext().getConfig().getNettyServerIp())
                        .setPort(getGlobalContext().getConfig().getNettyServerPort())
                        .setServerId(state.getServerId())
                        .setLastWriteId(state.getWriteCount().get())
                        .build())
                .build();
        toyCacheClient.talkAsync(toSlaveRequest, Lists.newArrayList(new SyncSlaveResponseCallback(getGlobalContext())));
        return Proto.MasterOfResponse.newBuilder()
                .setOk(true)
                .build();
    }
}
