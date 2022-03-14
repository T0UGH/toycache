package com.t0ugh.server.handler.impl.replicate;

import com.google.common.collect.Lists;
import com.t0ugh.client.ToyCacheClient;
import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.callback.SyncSlaveResponseCallback;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

import java.util.Map;

public class InnerStartSyncRequestHandler extends AbstractGenericsHandler<Proto.InnerStartSyncRequest, Proto.InnerStartSyncResponse> {

    public InnerStartSyncRequestHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.InnerStartSyncResponse doHandle(Proto.InnerStartSyncRequest innerStartSyncRequest) throws Exception {
        Map<Long, Long> progress = getGlobalContext().getGlobalState().getSlavesProgress();
        Map<Long, ToyCacheClient> clientMap = getGlobalContext().getGlobalState().getSlavesClient();
        long lastWriteId = getGlobalContext().getGlobalState().getWriteCount().get();
        for (Map.Entry<Long, Long> entry :progress.entrySet()) {
            Proto.SyncRequest.Builder builder = Proto.SyncRequest.newBuilder();
            long serverId = entry.getKey();
            long slaveLastWriteId = entry.getValue();
            // 只有slave的进度小于master的进度才需要处理
            if (slaveLastWriteId >= lastWriteId){
                break;
            }
            // 判断是快照还是缓存
            if(getGlobalContext().getRequestBuffer().minWriteId() <= slaveLastWriteId){
                // 发快照
                for (long i = slaveLastWriteId + 1; i <= lastWriteId; i ++){
                    builder.addSyncRequests(getGlobalContext().getRequestBuffer().get(i).get());
                }
            } else {
                // 发缓存
                builder.setDb(getGlobalContext().getStorage().toUnModifiableDB(lastWriteId));
            }
            builder.setLastWriteId(lastWriteId);
            builder.setClusterId(getGlobalContext().getGlobalState().getClusterId());
            builder.setIp(getGlobalContext().getConfig().getNettyServerIp());
            builder.setPort(getGlobalContext().getConfig().getNettyServerPort());
            builder.setServerId(getGlobalContext().getGlobalState().getServerId());
            clientMap.get(serverId).talkAsync(Proto.Request.newBuilder()
                            .setMessageType(Proto.MessageType.Sync)
                            .setSyncRequest(builder.build())
                    .build(), Lists.newArrayList(new SyncSlaveResponseCallback(getGlobalContext())));
        }
        return Proto.InnerStartSyncResponse.newBuilder().build();
    }
}
