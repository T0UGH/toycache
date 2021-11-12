package com.t0ugh.server.tick;

import com.google.common.collect.Lists;
import com.t0ugh.server.GlobalContext;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TickDriver {

    private final GlobalContext globalContext;
    private final ScheduledExecutorService executorService;
    private final List<Ticker> ticks;

    public TickDriver(GlobalContext globalContext) {
        this.globalContext = globalContext;
        executorService = Executors.newSingleThreadScheduledExecutor();
        ticks = Lists.newArrayList();
    }

    public void start() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ticks.forEach(Ticker::tick);
            }
        }, 0, globalContext.getConfig().getTickInterval(), TimeUnit.MILLISECONDS);
    }

    public void register(Ticker tick){
        ticks.add(tick);
    }

    public void registerAll(List<Ticker> t){
        ticks.addAll(t);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }

}
