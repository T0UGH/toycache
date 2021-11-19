package com.t0ugh.server.handler.impl.sortedset;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.impl.set.BaseSetHandlerTest;
import com.t0ugh.server.storage.MemoryComparableString;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class ZAddHandlerTest extends BaseSortedSetHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        List<DBProto.ComparableString> values = Lists.newArrayList(
                DBProto.ComparableString.newBuilder().setStringValue("H4").setScore(7).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H1").setScore(0).build()
        );

        List<MemoryComparableString> expected = Lists.newArrayList(
                MemoryComparableString.builder().stringValue("H4").score(7).build(),
                MemoryComparableString.builder().stringValue("H2").score(3).build(),
                MemoryComparableString.builder().stringValue("H3").score(1).build(),
                MemoryComparableString.builder().stringValue("H1").score(0).build()
        );
        test("Hi", values, 1, expected);
    }

    @Test
    public void test2() throws Exception {
        List<DBProto.ComparableString> values = Lists.newArrayList(
                DBProto.ComparableString.newBuilder().setStringValue("H4").setScore(7).build(),
                DBProto.ComparableString.newBuilder().setStringValue("H1").setScore(0).build()
        );

        List<MemoryComparableString> expected = Lists.newArrayList(
                MemoryComparableString.builder().stringValue("H4").score(7).build(),
                MemoryComparableString.builder().stringValue("H1").score(0).build()
        );
        test("Hx", values, 2, expected);
    }

    public void test(String key, List<DBProto.ComparableString> values, int added, List<MemoryComparableString> expected) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZAdd)
                .setZAddRequest(Proto.ZAddRequest.newBuilder()
                        .setKey(key)
                        .addAllValues(values)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.ZAdd).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.ZAdd, response);
        assertEquals(added, response.getZAddResponse().getAdded());
        Storage storage = testContext.getStorage();
        assertEquals(expected.size(), storage.backdoor().get(key).getSortedSetValue().backdoorS().size());
        int i = 0;
        for (MemoryComparableString comparableString: storage.backdoor().get(key).getSortedSetValue().backdoorS()) {
            assertEquals(comparableString.getStringValue(), expected.get(i).getStringValue());
            assertEquals(comparableString.getScore(), expected.get(i).getScore(), 0.0);
            i++;
        }
    }
}