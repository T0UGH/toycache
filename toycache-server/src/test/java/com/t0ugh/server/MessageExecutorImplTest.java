package com.t0ugh.server;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.callback.CallbackTestImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageExecutorImplTest extends BaseTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOneCmd() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Hello")
                        .setValue("World")).build();
        CallbackTestImpl testCallback = new CallbackTestImpl();
        testContext.getMessageExecutor().submit(request, testCallback);
        testContext.getMessageExecutor().submitAndWait(request, testCallback);
        assertEquals(2, testCallback.responseList.size());
        testContext.getMessageExecutor().submitAndWait(request, testCallback);
        assertEquals(3, testCallback.responseList.size());
    }
}