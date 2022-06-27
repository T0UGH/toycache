package com.t0ugh.server.zookeeper;

import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.ZKUtils;
import lombok.SneakyThrows;
import org.apache.zookeeper.*;

public class MasterChildWatcher implements Watcher {

    private ZooKeeper zooKeeper;
    private GlobalContext globalContext;
    private long followerId;

    public MasterChildWatcher(GlobalContext globalContext, long followerId){
        this.globalContext = globalContext;
        this.zooKeeper = globalContext.getZooKeeper();
        this.followerId = followerId;
    }

    public void onNodeDataChanged(){
        // 更新
        // todo: 这个process是在哪个线程中执行的
        zooKeeper.getData(ZKUtils.getFollowerPath(globalContext.getGlobalState().getGroupId(), followerId), false, (rc, path, ctx, data, stat) -> {
            GlobalContext globalContext1 = (GlobalContext)ctx;
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    // 重试
                    onNodeDataChanged();
                    break;
                case OK:
                    try {
                        ZKProto.ServerMeta serverMeta = ZKProto.ServerMeta.parseFrom(data);
                        // 主要就更新process
                        // todo: 这个更新交给主线程做
                        globalContext1.getGlobalState().getFollowerProcess().put(serverMeta.getServerId(), serverMeta.getLastWriteId());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
            }
        }, globalContext);
    }

    @SneakyThrows
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            onNodeDataChanged();
        } else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            // 从表中删掉
            // todo: 这个删除也交给主线程去做
            globalContext.getGlobalState().getFollowerProcess().remove(followerId);
            globalContext.getGlobalState().getFollowerClients().remove(followerId);
            // 删除watcher, 删除自己
            zooKeeper.removeWatches(
                    ZKUtils.getFollowerPath(globalContext.getGlobalState().getGroupId(), followerId),
                    this,
                    WatcherType.Any,
                    true
            );
        }
    }

    public void addWatch() throws InterruptedException, KeeperException {
        zooKeeper.addWatch(ZKUtils.getFollowerPath(globalContext.getGlobalState().getGroupId(), followerId), this, AddWatchMode.PERSISTENT);
    }
}
