package com.t0ugh.server.writeLog;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.sdk.proto.ValueObjects;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.tick.MessageExecutorTestImpl;
import com.t0ugh.server.utils.WriteLogUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class WriteLogExecutorTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试提交两条正常的写日志
     * 1. 能不能从文件中再将这两条写日志分别都读出来
     * */
    @Test
    public void testCommonRequest() throws Exception {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Hello")
                        .setValue("World")).build();
        Proto.Request request2 = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Hello")
                        .setValue("World2")).build();
        testContext.getWriteLogExecutor().submitAndWait(request);
        testContext.getWriteLogExecutor().submitAndWait(request2);
        OutputStream outputStream = testContext.getWriteLogOutputStream();
        outputStream.close();
        InputStream inputStream = new FileInputStream(WriteLogUtils.getWriteLogFilePath(testContext.getConfig().getWriteLogBaseFilePath()));
        Proto.Request readFromFile1 = Proto.Request.parseDelimitedFrom(inputStream);
        Proto.Request readFromFile2 = Proto.Request.parseDelimitedFrom(inputStream);
        assertEquals("World", readFromFile1.getSetRequest().getValue());
        assertEquals("World2", readFromFile2.getSetRequest().getValue());
        assertEquals(0, inputStream.available());
        inputStream.close();
    }

    /**
     * 测试提交一条写请求、一条重写请求、再一条写请求
     * 1. 第一条写请求被删掉了
     * 2. 后面的都有
     * 3. MessageExecutor能收到一条WriteLogFinish消息
     * */
    @Test
    public void testInnerRewriteLogRequest() throws Exception {
        MessageExecutorTestImpl messageExecutorTest = new MessageExecutorTestImpl();
        testContext.setMemoryOperationExecutor(messageExecutorTest);
        Proto.Request request1 = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Hello")
                        .setValue("World")).build();
        testContext.getWriteLogExecutor().submitAndWait(request1);
        Storage storage = testContext.getStorage();
        storage.backdoor().put("Hello", ValueObjects.newInstance("World"));
        storage.backdoor().put("Hi", ValueObjects.newInstance("World"));
        storage.expireBackdoor().put("Hello", System.currentTimeMillis() + 100000L);
        Proto.Request innerRewrite = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerRewriteLog)
                .setInnerRewriteLogRequest(Proto.InnerRewriteLogRequest.newBuilder()
                        .setDb(storage.toUnModifiableDB())
                        .build())
                .build();
        testContext.getWriteLogExecutor().submitAndWait(innerRewrite);
        Proto.Request request2 = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder()
                        .setKey("Haha")
                        .setValue("World2")).build();
        testContext.getWriteLogExecutor().submitAndWait(request2);
        OutputStream outputStream = testContext.getWriteLogOutputStream();
        outputStream.close();
        InputStream inputStream = new FileInputStream(WriteLogUtils.getWriteLogFilePath(testContext.getConfig().getWriteLogBaseFilePath()));
        List<Proto.Request> requests = Lists.newArrayList();
        while (inputStream.available()!=0){
            requests.add(Proto.Request.parseDelimitedFrom(inputStream));
        }
        assertEquals(4, requests.size());
        Map<Proto.MessageType, Integer> m = Maps.newHashMap();
        requests.forEach(r -> {
            m.put(r.getMessageType(), m.getOrDefault(r.getMessageType(), 0) + 1);

        });
        assertEquals(3, (int)m.get(Proto.MessageType.Set));
        assertEquals(1, (int)m.get(Proto.MessageType.Expire));
        inputStream.close();
        assertEquals(1, messageExecutorTest.requestList.size());
        assertEquals(Proto.MessageType.InnerRewriteLogFinish, messageExecutorTest.requestList.get(0).getMessageType());
    }
}