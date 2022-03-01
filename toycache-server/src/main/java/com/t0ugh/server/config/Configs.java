package com.t0ugh.server.config;

public class Configs {
    public static Config newDefaultConfig() {
        return Config.builder()
                .periodicalDeleteTick(25)
                .nettyServerPort(8114)
                .tickInterval(100)
                .dbBaseFilePath("D:\\tmp\\tcdb\\dev")
                .writeLogBaseFilePath("D:\\tmp\\tcwlog\\dev")
                .upperKeyLimitOfPeriodicalDelete(100).build();
    }

    public static Config newTestConfig() {
        return Config.builder()
                .periodicalDeleteTick(5)
                .nettyServerPort(8114)
                .tickInterval(100)
                .saveCheckLimit(5)
                .saveCheckTick(2)
                .rewriteLogSizeKBThreshold(1)
                .rewriteLogTick(2)
                .dbBaseFilePath("D:\\tmp\\tcdb\\test")
                .writeLogBaseFilePath("D:\\tmp\\tcwlog\\test")
                .upperKeyLimitOfPeriodicalDelete(10).build();
    }
}
