package com.t0ugh.server.zookeeper;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;

import java.util.concurrent.ExecutorService;

public class CreateFollowerToZKExecutor extends AbstractMessageExecutor {

    protected CreateFollowerToZKExecutor(GlobalContext globalContext, ExecutorService executorService) {
        super(globalContext, executorService);
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws Exception {
        // 需要删掉老的, 如果老的存在的话, 并创建一个新的。
        // 并且设置一顿各种值
//        getGlobalContext().getZooKeeper().delete();
//        getGlobalContext().getZooKeeper().create();
        return null;
    }
}
