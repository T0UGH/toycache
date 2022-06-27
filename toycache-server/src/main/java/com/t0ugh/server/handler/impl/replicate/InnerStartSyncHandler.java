package com.t0ugh.server.handler.impl.replicate;

import com.t0ugh.client.ToyCacheClient;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.storage.RequestBuffer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.InnerStartSync, checkExpire = false, handlerType= HandlerType.Other)
public class InnerStartSyncHandler extends AbstractGenericsHandler<Proto.InnerStartSyncRequest, Proto.InnerStartSyncResponse> {

    public InnerStartSyncHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.InnerStartSyncResponse doHandle(Proto.InnerStartSyncRequest innerStartSyncRequest) throws Exception {
        // 判断该给谁发什么，但是不要发太多
        Map<Long, Long> processes = getGlobalContext().getGlobalState().getFollowerProcess();
        Map<Long, ToyCacheClient> clients = getGlobalContext().getGlobalState().getFollowerClients();
        for(Map.Entry<Long, Long> process: processes.entrySet()){
            long currLastWriteId = process.getValue();
            long followerId = process.getKey();
            RequestBuffer requestBuffer = getGlobalContext().getRequestBuffer();
            Proto.SyncRequest.Builder syncRequestBuilder = Proto.SyncRequest.newBuilder().setServerId(followerId);
            if (requestBuffer.minWriteId() > currLastWriteId + 1){
                // 只能发快照了
                syncRequestBuilder.setDb(getGlobalContext().getStorage().toUnModifiableDB(getGlobalContext().getGlobalState().getWriteCount().get()));
            } else {
                // 找到对应的RequestBuffer中的数据, 发送过去
                List<Proto.Request> requests = requestBuffer.subList(currLastWriteId + 1);
                if (requests.size() > 0)
                    syncRequestBuilder.addAllSyncRequests(requests);
            }
            // 如果有快照或者如果有值得发送的Requests就发
            if (!Objects.isNull(syncRequestBuilder.getDb()) || syncRequestBuilder.getSyncRequestsCount() > 0){
                //发送
                Proto.Request request = Proto.Request.newBuilder()
                        .setMessageType(Proto.MessageType.Sync)
                        .setSyncRequest(syncRequestBuilder.build())
                        .build();
                getGlobalContext().getSendSyncExecutor().submit(request);
            }
        }
        return Proto.InnerStartSyncResponse.newBuilder().build();
    }
}
