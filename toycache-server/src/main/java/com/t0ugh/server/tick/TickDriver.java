package com.t0ugh.server.tick;

import java.util.List;

public interface TickDriver {
    void start();

    void register(Ticker tick);

    void registerAll(List<Ticker> t);

    void shutdown();

    void shutdownNow();
}
