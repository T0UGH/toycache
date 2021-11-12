package com.t0ugh.server.tick;

import com.google.common.collect.Lists;
import com.t0ugh.server.GlobalContext;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TickDriverImpl implements TickDriver{

    private final GlobalContext globalContext;
    private final ScheduledExecutorService executorService;
    private final List<Ticker> ticks;

    public TickDriverImpl(GlobalContext globalContext) {
        this.globalContext = globalContext;
        executorService = Executors.newSingleThreadScheduledExecutor();
        ticks = Lists.newArrayList();
    }

    @Override
    public void start() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ticks.forEach(Ticker::tick);
            }
        }, 0, globalContext.getConfig().getTickInterval(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void register(Ticker tick){
        ticks.add(tick);
    }

    @Override
    public void registerAll(List<Ticker> t){
        ticks.addAll(t);
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public void shutdownNow() {
        executorService.shutdownNow();
    }

}
