package com.t0ugh.server.utils;

import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class OtherUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRandomPick() {
        Set<String> all = Sets.newHashSet();
        all.add("a");
        all.add("b");
        all.add("c");
        all.add("d");
        all.add("e");
        all.add("f");
        all.add("g");
        Set<String> picked1 = OtherUtils.randomPick(3, all);
        System.out.println();
    }
}