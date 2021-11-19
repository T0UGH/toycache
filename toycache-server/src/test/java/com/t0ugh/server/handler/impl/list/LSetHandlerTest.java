package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LSetHandlerTest extends BaseListHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        test("Hi", 0, "NH0", true);
    }

    @Test
    public void test2() throws Exception {
        test("Hi", 3, "NH0", false);
    }

    @Test
    public void test3() throws Exception {
        test("Ha", 0, "NH0", false);
    }


    public void test(String key, int index, String newValue, boolean expectedOk) throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.LSet)
                .setLSetRequest(Proto.LSetRequest.newBuilder()
                        .setKey(key)
                        .setIndex(index)
                        .setNewValue(newValue)
                        .build())
                .build();
        Proto.Response response = testContext.getHandlerFactory().getHandler(Proto.MessageType.LSet).get().handle(request);
        TestUtils.assertOK(Proto.MessageType.LSet, response);
        assertEquals(expectedOk, response.getLSetResponse().getOk());
        if(expectedOk){
            Storage storage = testContext.getStorage();
            assertEquals(newValue, storage.backdoor().get(key).getListValue().get(index));
        }

    }
}