package com.t0ugh.server.handler.impl.multi;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MultiHandlerTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
        testContext.getStorage().backdoor().put("Counter", MemoryValueObject.newInstance("0"));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() throws Exception {
        Proto.MultiRequest multiRequest = Proto.MultiRequest.newBuilder()
                .addRequests(Proto.Request.newBuilder()
                        .setMessageType(Proto.MessageType.CheckGet)
                        .setCheckGetRequest(Proto.CheckGetRequest.newBuilder()
                                .setKey("Counter")
                                .setValue("0")
                                .build())
                        .build())
                .addRequests(Proto.Request.newBuilder()
                        .setMessageType(Proto.MessageType.Set)
                        .setSetRequest(Proto.SetRequest.newBuilder()
                                .setKey("Counter")
                                .setValue("1")
                                .build())
                        .build())
                .build();
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Multi)
                .setMultiRequest(multiRequest)
                .build();
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.Multi).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.Multi, response);
        assertEquals(2, response.getMultiResponse().getResponsesCount());
        List<Proto.Response> expectedResponses = Lists.newArrayList(
                Proto.Response.newBuilder()
                        .setMessageType(Proto.MessageType.CheckGet)
                        .setResponseCode(Proto.ResponseCode.OK)
                        .setCheckGetResponse(Proto.CheckGetResponse.newBuilder()
                                .setActualValue("0")
                                .setPass(true)
                                .build())
                        .build(),
                Proto.Response.newBuilder()
                        .setMessageType(Proto.MessageType.Set)
                        .setResponseCode(Proto.ResponseCode.OK)
                        .setSetResponse(Proto.SetResponse.newBuilder()
                                .setOk(true)
                                .build())
                        .build());
        for (int i = 0; i < expectedResponses.size(); i++) {
            assertEquals(expectedResponses.get(i), response.getMultiResponse().getResponses(i));
        }
    }
}