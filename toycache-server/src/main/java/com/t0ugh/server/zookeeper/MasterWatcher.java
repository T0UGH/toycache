package com.t0ugh.server.zookeeper;

import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.client.ToyCacheClient;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.ZKUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

@Slf4j
public class MasterWatcher implements Watcher {

    private ZooKeeper zooKeeper;
    private GlobalContext globalContext;

    public MasterWatcher(GlobalContext globalContext){
        this.globalContext = globalContext;
        this.zooKeeper = globalContext.getZooKeeper();
    }

    public void onNodeChildrenChanged(){
        zooKeeper.getChildren(ZKUtils.getFollowersPath(globalContext.getGlobalState().getGroupId()), false, (rc, path, ctx, children) -> {
            GlobalContext globalContext1 = (GlobalContext)ctx;
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    onNodeChildrenChanged();
                    break;
                case OK:
                    // 判断: 如果children变多了, 就给变多的那个添加一个Watcher, 并且查询一下数据并回填到该填的位置
                    for(String child: children){
                        long followerId = ZKUtils.getFollowerId(child);
                        if (!globalContext1.getGlobalState().getFollowerProcess().containsKey(followerId)){
                            // 就查询一下这个节点真实的值, 查到值之后更新到map里, 还要更新到ToyCache里
                            onGetFollower(followerId);
                        }
                    }
                    // 如果children变少了, 不用管, 因为下面的Watcher会自动处理
            }
        }, globalContext);
    }

    public void onGetFollower(long followerId){
        zooKeeper.getData(ZKUtils.getFollowerPath(globalContext.getGlobalState().getGroupId(), followerId), false, (rc, path, ctx, data, stat) -> {
            GlobalContext globalContext1 = (GlobalContext)ctx;
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    onGetFollower(followerId);
                    break;
                case OK:
                    // 如果ok了就用data更新一下，并且添加一个子节点的Watcher。
                    try {
                        ZKProto.ServerMeta serverMeta = ZKProto.ServerMeta.parseFrom(data);

                        globalContext1.getMemoryOperationExecutor().submit(
                                Proto.Request.newBuilder()
                                        .setMessageType(Proto.MessageType.InnerMasterUpdateFollowerProcess)
                                        .setInnerMasterUpdateFollowerProcessRequest(Proto.InnerMasterUpdateFollowerProcessRequest.newBuilder()
                                                .setGroupId(serverMeta.getGroupId())
                                                .setLastWriteId(serverMeta.getLastWriteId())
                                                .setServerId(serverMeta.getServerId())
                                                .setIp(serverMeta.getServerIp())
                                                .setPort(serverMeta.getServerPort())
                                                .build())
                                        .build()
                        );
                        new MasterChildWatcher(globalContext1, serverMeta.getServerId()).addWatch();
                    } catch (InvalidProtocolBufferException | KeeperException e) {
                        log.error("", e);
                        onGetFollower(followerId);
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
            }
        }, globalContext);
    }

    @Override
    public void process(WatchedEvent event) {
        //简单来说, 如果子节点发生了变化, 读取全部的子节点并且全部更新
        if (event.getType() == Event.EventType.NodeChildrenChanged){
            onNodeChildrenChanged();
        }
    }

    public void addWatch() throws InterruptedException, KeeperException {
        zooKeeper.addWatch(ZKUtils.getFollowersPath(globalContext.getGlobalState().getGroupId()), this, AddWatchMode.PERSISTENT);
    }
}
