package com.t0ugh.server.zookeeper;

import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.utils.ZKUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;
import static org.junit.Assert.*;

public class FollowerWatcherTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
        testContext.setZooKeeper(new ZooKeeper(testContext.getConfig().getZookeeperServerIp(), testContext.getConfig().getZookeeperServerPort(), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getType().toString());
            }
        }));
    }

    @Test
    public void test() throws InterruptedException, KeeperException, IOException {
        FollowerWatcher followerWatcher = new FollowerWatcher(testContext);
        followerWatcher.addWatch();
        Thread thread = new Thread(new RunnableZK(new ZooKeeper("127.0.0.1", 2181, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
            }
        })));
        thread.start();
        Thread.sleep(200);
        System.out.println();
    }

    @AllArgsConstructor
    private class RunnableZK implements Runnable {

        private final ZooKeeper zooKeeper;

        @Override
        public void run() {
            try{
                ZKProto.ServerMeta serverMeta1 = ZKProto.ServerMeta.newBuilder()
                        .setEpoch(0L)
                        .build();
                zooKeeper.create(ZKUtils.getMasterPath(testContext.getGlobalState().getGroupId()), serverMeta1.toByteArray(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                Thread.sleep(100);
                ZKProto.ServerMeta serverMeta2 = ZKProto.ServerMeta.newBuilder()
                        .setEpoch(1L)
                        .build();
                zooKeeper.setData(ZKUtils.getMasterPath(testContext.getGlobalState().getGroupId()), serverMeta2.toByteArray(), -1);
            } catch (Exception e){

                e.printStackTrace();
            }

        }
    }
}