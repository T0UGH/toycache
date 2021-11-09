package com.t0ugh.server.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Config {
    // 定期删除策略最多一次删除多少个键
    private int upperKeyLimitOfPeriodicalDelete;
    private int periodicalDeleteTick;
    private int nettyServerPort;
}
