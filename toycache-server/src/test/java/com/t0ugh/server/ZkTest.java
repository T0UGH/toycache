package com.t0ugh.server;

import com.t0ugh.server.utils.ZKUtils;
import org.apache.zookeeper.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ZkTest {
    @Test
    public void testCon() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1", 2181, null);
//        zooKeeper.create("/toycache", new byte[0], OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        zooKeeper.create("/toycache/group-01", new byte[0], OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        zooKeeper.create("/toycache/group-01/followers", new byte[0], OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        byte[] data = "{\"nodeId\": \"1\", \"epoch\": \"1\", \"writeId\":\"0\"}".getBytes(StandardCharsets.UTF_8);
        zooKeeper.create("/toycache/group-01/followers/follower-01", data, OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Thread.sleep(5000);

    }

    @Test
    public void testGetChildren() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1", 2181, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.toString());
            }
        });
        zooKeeper.addWatch(ZKUtils.getFollowersPath(1L), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.toString());
            }
        }, AddWatchMode.PERSISTENT);
        Thread.sleep(1000000000);
        System.out.println();
    }
}
