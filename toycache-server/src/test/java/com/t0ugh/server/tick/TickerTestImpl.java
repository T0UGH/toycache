package com.t0ugh.server.tick;

public class TickerTestImpl implements Ticker {

    public int count = 0;

    @Override
    public void tick() {
        count ++;
    }
}

