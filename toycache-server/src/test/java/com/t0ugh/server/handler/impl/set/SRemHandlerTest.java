package com.t0ugh.server.handler.impl.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SRemHandlerTest extends BaseSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", Sets.newHashSet("H1", "H2", "H4"), 2, true, Sets.newHashSet("H3"));
    }

    @Test
    public void test3() throws Exception {
        test("Hx", Sets.newHashSet("H1", "H2", "H4"), 0, false, null);
    }

    public void test(String key, Set<String> members, int deleted, boolean checkStorage, Set<String> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.SRem)
                .setSRemRequest(Proto.SRemRequest.newBuilder()
                        .setKey(key)
                        .addAllMembers(members)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.SRem).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.SRem, response);
        assertEquals(deleted, response.getSRemResponse().getDeleted());
        if(checkStorage){
            Storage storage = testContext.getStorage();
            assertEquals(expected.size(), storage.backdoor().get(key).getSetValue().size());
            assertTrue(expected.containsAll(storage.backdoor().get(key).getSetValue()));
        }
    }
}