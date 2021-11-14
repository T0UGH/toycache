package com.t0ugh.server;

import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.enums.SaveState;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class GlobalState {
    private AtomicInteger updateCount;
    private RewriteLogState rewriteLogState;
    private SaveState saveState;
    public static GlobalState newInstance(){
        return GlobalState.builder()
                .saveState(SaveState.Idle)
                .rewriteLogState(RewriteLogState.Normal)
                .updateCount(new AtomicInteger(0))
                .build();
    }
}
