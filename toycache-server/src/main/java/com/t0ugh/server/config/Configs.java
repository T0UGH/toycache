package com.t0ugh.server.config;

public class Configs {
    public static Config newDefaultConfig() {
        return Config.builder()
                .periodicalDeleteTick(25)
                .nettyServerPort(8114)
                .upperKeyLimitOfPeriodicalDelete(100).build();
    }
}
