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
    private long groupId;
    private String masterIp;
    private int masterPort;
    private long epoch;
    private Map<Long, ToyCacheClient> followerClients; // 用于与slaves通信
    private ToyCacheClient masterClient; //用于与master通信
    private Map<Long, Long> followerProcess; // slave的serverId -> slave当前同步到的writeId是多少
    // todo: 初始化不全
    public static GlobalState newInstance(long serverId, long groupId){
        return GlobalState.builder()
                .saveState(SaveState.Idle)
                .rewriteLogState(RewriteLogState.Normal)
                .writeCount(new AtomicLong(0))//todo: 不应该直接设为0，应该根据读取到的日志条数进行设置
                .masterState(MasterState.Master)
                .serverId(serverId)
                .groupId(groupId)
                .epoch(0) //todo: 不应该直接设为0,应该根据读取到的日志进行设置
                .followerProcess(Maps.newConcurrentMap())
                .followerClients(Maps.newConcurrentMap())
                .build();
    }
}
