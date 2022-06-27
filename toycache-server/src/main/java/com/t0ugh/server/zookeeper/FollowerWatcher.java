package com.t0ugh.server.zookeeper;

import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.MasterState;
import com.t0ugh.server.utils.ZKUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.Map;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

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
                        // todo: 这个更新交给主线程去做
                        globalContext.getGlobalState().setEpoch(serverMeta.getEpoch());
                    } catch (InvalidProtocolBufferException e) {
                        log.error("", e);
                        onNodeChangeOrCreate();
                    }
                    // todo: default怎么办
            }
        }, globalContext);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDataChanged || watchedEvent.getType() == Event.EventType.NodeCreated) {
            // 更新
            onNodeChangeOrCreate();
        } else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            onMasterDelete();
        }
    }

    public void onMasterDelete(){
        // 开始选举
        zooKeeper.getChildren(ZKUtils.getFollowersPath(globalContext.getGlobalState().getGroupId()), false, new AsyncCallback.Children2Callback() {
            @Override
            public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS:
                        // 重试
                        onMasterDelete();
                        break;
                    case OK:
                        GlobalContext globalContext1 = (GlobalContext)ctx;
                        globalContext1.getGlobalState().getFollowerProcessForVote().clear();
                        globalContext1.getGlobalState().setEpoch(globalContext1.getGlobalState().getEpoch() + 1);
                        globalContext1.getZkState().setVoteSize(children.size());
                        for(String child: children){
                            onVote(child);
                        }

                }
            }
        }, globalContext);
    }

    public void onVote(String childName){
        zooKeeper.getData(ZKUtils.getFollowersPath(globalContext.getGlobalState().getGroupId()), false, new AsyncCallback.DataCallback() {
            @SneakyThrows
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS:
                        // 重试
                        onVote((String)ctx);
                        break;
                    case OK:
                        ZKProto.ServerMeta serverMeta = ZKProto.ServerMeta.parseFrom(data);
                        globalContext.getGlobalState().getFollowerProcessForVote().put(serverMeta.getServerId(), serverMeta.getLastWriteId());
                        if (globalContext.getGlobalState().getFollowerProcessForVote().size() == globalContext.getZkState().getVoteSize()){
                            long max = globalContext.getGlobalState().getWriteCount().get();
                            long maxId = globalContext.getGlobalState().getServerId();
                            for (Map.Entry<Long, Long> entry: globalContext.getGlobalState().getFollowerProcessForVote().entrySet()){
                                long curr = entry.getValue();
                                if (entry.getKey() == globalContext.getGlobalState().getServerId()){
                                    continue;
                                }
                                if (curr > max){
                                    max = curr;
                                    maxId = entry.getKey();
                                }
                            }
                            // 如果我就是最合适的
                            if (maxId == globalContext.getGlobalState().getServerId()){
                                becomeMaster();
                            }
                        }
                }
            }
        }, childName);
    }

    public void becomeMaster() throws InterruptedException, KeeperException {
        globalContext.getGlobalState().setMasterState(MasterState.Master);
        ZKProto.ServerMeta serverMeta = ZKProto.ServerMeta.newBuilder()
                .setEpoch(globalContext.getGlobalState().getEpoch())
                .setServerPort(globalContext.getConfig().getNettyServerPort())
                .setServerIp(globalContext.getConfig().getNettyServerIp())
                .setServerId(globalContext.getGlobalState().getServerId())
                .setLastWriteId(globalContext.getGlobalState().getWriteCount().get())
                .setGroupId(globalContext.getGlobalState().getGroupId())
                .build();
        // 创建主节点
        zooKeeper.create(ZKUtils.getMasterPath(globalContext.getGlobalState().getGroupId()), serverMeta.toByteArray(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        // 删除从节点
        zooKeeper.delete(ZKUtils.getFollowerPath(globalContext.getGlobalState().getGroupId(), globalContext.getGlobalState().getServerId()), -1);
    }

    public void addWatch() throws InterruptedException, KeeperException {
        zooKeeper.addWatch(ZKUtils.getMasterPath(globalContext.getGlobalState().getGroupId()), this, AddWatchMode.PERSISTENT);
    }
}
