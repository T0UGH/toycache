package com.t0ugh.server.tick;

import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.utils.ZKUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.junit.Before;
import org.junit.Test;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;
import static org.junit.Assert.*;

public class ZooKeeperHeartBeatTickerTest extends BaseTest {

    ZooKeeperHeartBeatTicker zooKeeperHeartBeatTicker;
    TickDriverTestImpl tickDriver;

    @Before
    public void setUp() throws Exception {
        testContext.setZooKeeper(new ZooKeeper(testContext.getConfig().getZookeeperServerIp(), testContext.getConfig().getZookeeperServerPort(), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getType().toString());
            }
        }));
        tickDriver = new TickDriverTestImpl(testContext);
        zooKeeperHeartBeatTicker = new ZooKeeperHeartBeatTicker(testContext);
        tickDriver.register(zooKeeperHeartBeatTicker);
        // todo: 这个应该在代码中干其实
        testContext.getZooKeeper().create(ZKUtils.getMasterPath(testContext.getGlobalState().getGroupId()), new byte[0], OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    @Test
    // 这个加断点可能导致zk的session expire，不加断点可以过
    public void tick() throws InterruptedException, KeeperException, InvalidProtocolBufferException {
        for(int i = 0; i < testContext.getConfig().getZookeeperHeartBeatTick(); i ++){
            tickDriver.tickManually();
        }
        Thread.sleep(200);
        byte[] data = testContext.getZooKeeper().getData(ZKUtils.getMasterPath(testContext.getGlobalState().getGroupId()), null, null);
        ZKProto.ServerMeta meta = ZKProto.ServerMeta.parseFrom(data);
        assertEquals(meta.getEpoch(), testContext.getGlobalState().getEpoch());
    }
}