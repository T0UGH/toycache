package com.t0ugh.server.tick;

import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.MessageUtils;
import com.t0ugh.server.utils.WriteLogUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RewriteLogTicker implements Ticker{

    private int count;
    private final ExecutorService executorService;
    private final GlobalContext globalContext;
    private final int interval;

    public RewriteLogTicker(GlobalContext globalContext) {
        executorService = Executors.newSingleThreadExecutor();
        this.globalContext = globalContext;
        this.interval = globalContext.getConfig().getRewriteLogTick();
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
                if(sizeExceedsThreshold()){
                    globalContext.getMemoryOperationExecutor().submit(MessageUtils.newRewriteLogRequest());

                }
                count = 0;
            }
        });
    }

    private boolean sizeExceedsThreshold() {
        File file = new File(WriteLogUtils.getWriteLogFilePath(globalContext.getConfig().getWriteLogBaseFilePath()));
        return  file.length() >= (globalContext.getConfig().getRewriteLogSizeKBThreshold() * 1024L);
    }
}
