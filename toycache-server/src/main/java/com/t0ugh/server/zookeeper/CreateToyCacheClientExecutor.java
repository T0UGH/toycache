package com.t0ugh.server.zookeeper;

import com.t0ugh.client.ToyCacheClient;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class CreateToyCacheClientExecutor extends AbstractMessageExecutor {

    protected CreateToyCacheClientExecutor(GlobalContext globalContext, ExecutorService executorService) {
        super(globalContext, executorService);
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws Exception {
        // 创建并且注册上
        if (Objects.equals(request.getMessageType(), Proto.MessageType.InnerCreateClient)){
            Proto.InnerCreateClientRequest createClientRequest = request.getInnerCreateClientRequest();
            getGlobalContext().getGlobalState().getFollowerClients()
                    .put(createClientRequest.getServerId(), new ToyCacheClient(createClientRequest.getIp(), createClientRequest.getPort()));
        }
        return null;
    }
}
