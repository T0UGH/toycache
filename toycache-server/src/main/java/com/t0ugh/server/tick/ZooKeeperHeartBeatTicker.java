package com.t0ugh.server.tick;

import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.GlobalState;
import com.t0ugh.server.enums.MasterState;

import com.t0ugh.server.utils.ZKUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZooKeeperHeartBeatTicker implements Ticker{
    private int count;
    private final ExecutorService executorService;
    private final GlobalContext globalContext;
    private final GlobalState globalState;
    private final int interval;
    private final ZooKeeper zooKeeper;

    public ZooKeeperHeartBeatTicker(GlobalContext globalContext) {
        executorService = Executors.newSingleThreadExecutor();
        this.globalContext = globalContext;
        this.globalState = globalContext.getGlobalState();
        this.interval = globalContext.getConfig().getZookeeperHeartBeatTick();
        this.zooKeeper  = globalContext.getZooKeeper();
    }

    @Override
    public void tick() {
        executorService.submit(() -> {
            count ++;
            if(count >= interval) {
                ZKProto.ServerMeta serverMeta = ZKProto.ServerMeta.newBuilder()
                        .setServerId(globalState.getServerId())
                        .setLastWriteId(globalState.getWriteCount().get())
                        .setEpoch(globalState.getEpoch())
                        .setGroupId(globalState.getGroupId())
                        .setServerIp(globalContext.getConfig().getNettyServerIp())
                        .setServerPort(globalContext.getConfig().getNettyServerPort())
                        .build();
                // 主从的path不一样
                try {
                    if (globalContext.getGlobalState().getMasterState() == MasterState.Master) {
                        zooKeeper.setData(
                                ZKUtils.getMasterPath(globalContext.getGlobalState().getGroupId()),
                                serverMeta.toByteArray(),
                                -1);
                    } else {
                        zooKeeper.setData(
                                ZKUtils.getFollowerPath(globalContext.getGlobalState().getGroupId(), globalState.getServerId()),
                                serverMeta.toByteArray(),
                                -1);
                    }
                // todo: 异常处理
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count = 0;
            }
        });
    }
}
