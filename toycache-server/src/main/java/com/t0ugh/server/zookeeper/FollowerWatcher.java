package com.t0ugh.server.zookeeper;

import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.ZKUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

@Slf4j
public class FollowerWatcher implements Watcher {

    private ZooKeeper zooKeeper;
    private GlobalContext globalContext;

    public FollowerWatcher(GlobalContext globalContext){
        this.globalContext = globalContext;
        this.zooKeeper = globalContext.getZooKeeper();
    }

    public void onNodeChangeOrCreate(){
        // todo: 这个process是在哪个线程中执行的
        zooKeeper.getData(ZKUtils.getMasterPath(globalContext.getGlobalState().getGroupId()), false, (rc, path, ctx, data, stat) -> {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    // 重试
                    onNodeChangeOrCreate();
                    break;
                case OK:
                    try {
                        ZKProto.ServerMeta serverMeta = ZKProto.ServerMeta.parseFrom(data);
                        GlobalContext globalContext = (GlobalContext) ctx;
                        // todo: 需不需要更新masterId，可能也不需要其实
                        // todo: 这里会更新epoch，需不需要把epoch设置为线程安全的，起码设置为volatile的
                        // todo: 这个回调又是在哪个线程中执行的? 根据博客显示，这些回调是在EventThread这个zk内部的特殊线程中执行的
                        globalContext.getGlobalState().setEpoch(serverMeta.getEpoch());
                    } catch (InvalidProtocolBufferException e) {
                        log.error("", e);
                        onNodeChangeOrCreate();
                    }
            }
        }, globalContext);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDataChanged || watchedEvent.getType() == Event.EventType.NodeCreated) {
            // 更新
            onNodeChangeOrCreate();
        } else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            // 切换为Candidate，然后开始选举
            // todo: 实现一下
        }
    }

    public void addWatch() throws InterruptedException, KeeperException {
        zooKeeper.addWatch(ZKUtils.getMasterPath(globalContext.getGlobalState().getGroupId()), this, AddWatchMode.PERSISTENT);
    }
}
