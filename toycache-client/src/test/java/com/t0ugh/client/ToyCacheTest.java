package com.t0ugh.client;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToyCacheTest {

    private ToyCache toyCache;

    @Before
    public void setUp() {
        toyCache = new ToyCache("127.0.0.1", 8114);
    }

    @Test
    public void exists() throws InterruptedException {
        assertFalse(toyCache.exists("haha"));
    }

    @Test
    public void del() throws InterruptedException {
        assertFalse(toyCache.del("haha"));
    }
}