package com.t0ugh.sdk.proto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProtoTest {

    @Before
    public void setUp() throws Exception {
    }

    @Deprecated
    @Test
    public void testLoadClass() throws ClassNotFoundException {
        Class.forName("com.t0ugh.sdk.proto.Proto$SetRequest");
    }

    @After
    public void tearDown() throws Exception {

    }
}