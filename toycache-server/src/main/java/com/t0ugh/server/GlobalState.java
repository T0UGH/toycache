package com.t0ugh.server;

import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.enums.TransactionState;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class GlobalState {
    private AtomicInteger updateCount;
    private RewriteLogState rewriteLogState;
    private SaveState saveState;
    private TransactionState transactionState;
    public static GlobalState newInstance(){
        return GlobalState.builder()
                .saveState(SaveState.Idle)
                .rewriteLogState(RewriteLogState.Normal)
                .updateCount(new AtomicInteger(0))
                .build();
    }
}
