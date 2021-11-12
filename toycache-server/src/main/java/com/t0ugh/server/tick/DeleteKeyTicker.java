package com.t0ugh.server.tick;

import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.MessageUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteKeyTicker implements Ticker {
    private int count;
    private final ExecutorService executorService;
    private final GlobalContext globalContext;
    private final int interval;

    public DeleteKeyTicker(GlobalContext globalContext) {
        executorService = Executors.newSingleThreadExecutor();
        this.globalContext = globalContext;
        this.interval = globalContext.getConfig().getPeriodicalDeleteTick();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }

    @Override
    public void tick() {
        executorService.submit(() -> {
            count ++;
            if(count >= interval) {
                globalContext.getMessageExecutor().submit(MessageUtils.newInnerClearExpireRequest());
                count = 0;
            }
        });
    }
}
