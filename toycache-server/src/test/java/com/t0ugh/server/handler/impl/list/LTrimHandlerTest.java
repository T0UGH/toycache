package com.t0ugh.server.handler.impl.list;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class LTrimHandlerTest extends BaseListHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", 0, 1,true, Lists.newArrayList("H1", "H2"));
    }

    @Test
    public void test2() throws Exception {
        test("Hi", 1, 1,true, Lists.newArrayList("H2"));
    }

    @Test
    public void test3() throws Exception {
        test("Hi", -2, -1,true, Lists.newArrayList("H2", "H3"));
    }

    @Test
    public void test4() throws Exception {
        test("Hi", -1, 100,true, Lists.newArrayList("H3"));
    }

    @Test
    public void test5() throws Exception {
        test("Hi", 2, 1,false, Lists.newArrayList("H1", "H2", "H3"));
    }

    @Test
    public void test6() throws Exception {
        test("Hi", 100, 101,false, Lists.newArrayList("H1", "H2", "H3"));
    }

    @Test
    public void test7() throws Exception {
        test("Ha",  1, 1,false, null);
    }


    public void test(String key, int start, int end, boolean ok, List<String> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LTrim)
                .setLTrimRequest(Proto.LTrimRequest.newBuilder()
                        .setKey(key)
                        .setStart(start)
                        .setEnd(end)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LTrim).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LTrim, response);
        assertEquals(ok, response.getLTrimResponse().getOk());
        Storage storage = testContext.getStorage();
        if (!Objects.isNull(expected)){
            assertEquals(expected.size(), storage.backdoor().get(key).getListValue().size());
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i), storage.backdoor().get(key).getListValue().get(i));
            }
        } else {
            assertTrue(Objects.isNull(storage.backdoor().get(key)));
        }

    }
}