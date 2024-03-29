package com.t0ugh.server.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Config {
    // 定期删除策略最多一次删除多少个键
    private int upperKeyLimitOfPeriodicalDelete;
    private int periodicalDeleteTick;
    private String nettyServerIp;
    private int nettyServerPort;
    private String zookeeperServerIp;
    private int zookeeperServerPort;
    // tick间隔, 单位毫秒(MILLISECONDS)
    private int tickInterval;
    // 一个目录
    private String dbBaseFilePath;
    private String writeLogBaseFilePath;
    // 多少个tick同步一次follower
    private int syncFollowerTick;
    // 多少个tick检查一次存盘
    private int saveCheckTick;
    // 处理了至少多少个写请求才存盘
    private int saveCheckLimit;
    // 多少个tick检查一次是否需要重写日志
    private int rewriteLogTick;
    // 多少个tick往ZK发一次心跳
    private int zookeeperHeartBeatTick;
    // 如果写日志超过多少KB就重写
    private int rewriteLogSizeKBThreshold;
    // buffer中最多存储多少个Request
    private int maxBufferSize;
    // 服务器的唯一标识符
    private int serverId;
    // 运行Id, 用来标识一组集群服务器
    private int groupId;
    // 一个特殊的key
    private String noopKeyForRewriteLog;
}
