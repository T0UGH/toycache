package com.t0ugh.server.zookeeper;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;
import com.t0ugh.server.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * 负责往其他节点发送同步消息的Executor
 * */
public class SendSyncExecutor extends AbstractMessageExecutor {

    protected SendSyncExecutor(GlobalContext globalContext, ExecutorService executorService) {
        super(globalContext, executorService);
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws Exception {
        if(Objects.equals(Proto.MessageType.Sync, request.getMessageType())){
            Proto.SyncRequest syncRequest = request.getSyncRequest();
            // 有可能不存在, 因为ToyCacheClient的创建有可能滞后
            if (getGlobalContext().getGlobalState().getFollowerClients().containsKey(syncRequest.getServerId()))
                getGlobalContext().getGlobalState().getFollowerClients().get(syncRequest.getServerId()).talkAsync(request, new ArrayList<>());
        }
        // todo: 直接null不太好吧
        return null;
    }
}
