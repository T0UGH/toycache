package com.t0ugh.server.tick;

import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.MessageUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaveTicker implements Ticker{
    private int count;
    private final ExecutorService executorService;
    private final GlobalContext globalContext;
    private final int interval;
    private final int saveCheckLimit;
    private int lastUpdateCount;

    public SaveTicker(GlobalContext globalContext) {
        executorService = Executors.newSingleThreadExecutor();
        this.globalContext = globalContext;
        this.interval = globalContext.getConfig().getSaveCheckTick();
        this.saveCheckLimit = globalContext.getConfig().getSaveCheckLimit();
        lastUpdateCount = 0;
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
                int updateCount = globalContext.getGlobalState().getUpdateCount().get();
                if(updateCount - lastUpdateCount >= saveCheckLimit){
                     globalContext.getMemoryOperationExecutor().submit(MessageUtils.newStartSaveRequest());
                 }
                lastUpdateCount = updateCount;
                count = 0;
            }
        });
    }
}
