package com.t0ugh.server;

import com.google.common.collect.Maps;
import com.t0ugh.client.ToyCacheClient;
import com.t0ugh.server.enums.MasterState;
import com.t0ugh.server.enums.RewriteLogState;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.enums.TransactionState;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
public class GlobalState {
    private AtomicLong writeCount;
    private RewriteLogState rewriteLogState;
    private SaveState saveState;
    private TransactionState transactionState;
    private MasterState masterState;
    private long serverId;
    private long clusterId;
    private String masterIp;
    private int masterPort;
    private Map<Long, ToyCacheClient> slavesClient; // 用于与slaves通信
    private ToyCacheClient masterClient; //用于与master通信
    private Map<Long, Long> slavesProgress; // slave的serverId -> slave当前同步到的writeId是多少
    public static GlobalState newInstance(long serverId, long clusterId){
        return GlobalState.builder()
                .saveState(SaveState.Idle)
                .rewriteLogState(RewriteLogState.Normal)
                .writeCount(new AtomicLong(0))
                .masterState(MasterState.Master)
                .serverId(serverId)
                .clusterId(clusterId)
                .slavesProgress(Maps.newHashMap())
                .build();
    }
}
