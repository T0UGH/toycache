package com.t0ugh.server.handler.impl.sort;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.storage.MemoryComparableString;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ZRemHandlerTest extends BaseSortedSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        List<MemoryComparableString> expected = Lists.newArrayList(
                MemoryComparableString.builder().stringValue("H3").score(1).build());
        test("Hi", Sets.newHashSet("H1", "H2", "G1"), 2, true, expected);
    }

    @Test
    public void test2() throws Exception {
        test("Hx", Sets.newHashSet("H1", "H2", "G1"), 0, false, null);
    }

    public void test(String key, Set<String> members, int deleted, boolean checkStorage, List<MemoryComparableString> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZRem)
                .setZRemRequest(Proto.ZRemRequest.newBuilder()
                        .setKey(key)
                        .addAllMembers(members)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.ZRem).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.ZRem, response);
        assertEquals(deleted, response.getZRemResponse().getDeleted());
        if(checkStorage){
            Storage storage = testContext.getStorage();
            assertEquals(expected.size(), storage.backdoor().get(key).getSortedSetValue().backdoorS().size());
            int i = 0;
            for (MemoryComparableString mcs: storage.backdoor().get(key).getSortedSetValue().backdoorS()) {
                assertEquals(expected.get(i).getStringValue(), mcs.getStringValue());
                assertEquals(expected.get(i).getScore(), mcs.getScore(), 0.0);
            }
        }
    }
}