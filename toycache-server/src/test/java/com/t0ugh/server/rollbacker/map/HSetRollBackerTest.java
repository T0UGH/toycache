package com.t0ugh.server.rollbacker.map;

import com.google.common.collect.Maps;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class HSetRollBackerTest extends BaseMapRollBackerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试提交一条HSetRequest，然后回滚
     * */
    @Test
    public void test() throws Exception {
        Map<String, String> origin = Maps.newHashMap(testContext.getStorage().backdoor().get("test").getMapValue());
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HSet)
                .setHSetRequest(Proto.HSetRequest.newBuilder()
                        .setKey("test")
                        .setField("Hello")
                        .setValue("world")
                        .build())
                .build();
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(Proto.MessageType.HSet).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HSet).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(Proto.MessageType.HSet, response);
        assertTrue(response.getHSetResponse().getOk());
        rollBacker.rollBack();
        TestUtils.assertMapEquals(origin, Maps.newHashMap(testContext.getStorage().backdoor().get("test").getMapValue()));
    }

    /**
     * 测试对ValueTypeNotMatch的反应
     * */
    @Test
    public void test1() throws Exception {
        testContext.getStorage().backdoor().put("test2", MemoryValueObject.newInstance("test2"));
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HSet)
                .setHSetRequest(Proto.HSetRequest.newBuilder()
                        .setKey("test2")
                        .setField("Hello")
                        .setValue("world")
                        .build())
                .build();
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(Proto.MessageType.HSet).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HSet).get();
        Proto.Response response = handler.handle(request);
        assertEquals(Proto.ResponseCode.ValueTypeNotMatch, response.getResponseCode());
        rollBacker.rollBack();
        assertEquals("test2", testContext.getStorage().get("test2").get());
    }

    /**
     * 测试对InValidParam的反应
     * */
    @Test
    public void test2() throws Exception {
        Map<String, String> origin = Maps.newHashMap(testContext.getStorage().backdoor().get("test").getMapValue());
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.HSet)
                .setHSetRequest(Proto.HSetRequest.newBuilder()
                        .setKey("test")
                        .setField("")
                        .setValue("")
                        .build())
                .build();
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(Proto.MessageType.HSet).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(Proto.MessageType.HSet).get();
        Proto.Response response = handler.handle(request);
        assertEquals(Proto.ResponseCode.InvalidParam, response.getResponseCode());
        rollBacker.rollBack();
        TestUtils.assertMapEquals(origin, Maps.newHashMap(testContext.getStorage().backdoor().get("test").getMapValue()));
    }
}