package com.t0ugh.server.tick;

public class TickableTestImpl implements Tickable{

    public int count = 0;

    @Override
    public void tick() {
        count ++;
    }
}

