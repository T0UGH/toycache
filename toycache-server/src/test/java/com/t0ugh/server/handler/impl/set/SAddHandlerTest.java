package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class SAddHandlerTest extends BaseSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", Sets.newHashSet("H1", "H4", "H5"), 2, Sets.newHashSet("H1", "H2", "H3", "H4", "H5"));
    }

    @Test
    public void test2() throws Exception {
        test("Hx", Sets.newHashSet("H1", "H4", "H5"), 3, Sets.newHashSet("H1", "H4", "H5"));
    }

    @Test
    public void test3() throws Exception {
        test("Hi", Sets.newHashSet("H1"), 0, Sets.newHashSet("H1", "H2", "H3"));
    }

    public void test(String key, Set<String> values, int added, Set<String> expected)  throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SAdd)
                .setSAddRequest(Proto.SAddRequest.newBuilder()
                        .setKey(key)
                        .addAllSetValue(values))
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SAdd).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SAdd, response);
        assertEquals(added, response.getSAddResponse().getAdded());
        Storage storage = testContext.getStorage();
        if (!Objects.isNull(expected)){
            assertEquals(expected.size(), storage.backdoor().get(key).getSetValue().size());
        } else {
            assertTrue(Objects.isNull(storage.backdoor().get(key)));
        }
    }
}