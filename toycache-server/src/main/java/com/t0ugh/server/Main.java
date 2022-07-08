package com.t0ugh.server;

import lombok.Builder;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        ToyCacheServer toyCacheServer = new ToyCacheServer();
        Signal sg = new Signal("TERM"); // kill -15 pid
        // 监听信号量
        Signal.handle(sg, signal -> System.exit(0));
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(StopRunnable.builder().toyCacheServer(toyCacheServer).build()));

        toyCacheServer.startDev();
    }

}

@Builder
class StopRunnable implements Runnable{

    private ToyCacheServer toyCacheServer;

    @Override
    public void run() {
        toyCacheServer.stop();
    }
}