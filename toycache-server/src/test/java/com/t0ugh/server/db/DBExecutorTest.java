package com.t0ugh.server.db;

import com.google.common.collect.Maps;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.sdk.proto.ValueObjects;
import com.t0ugh.server.BaseTest;
import com.t0ugh.server.tick.MessageExecutorTestImpl;
import com.t0ugh.server.utils.DBUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.*;

public class DBExecutorTest extends BaseTest {

    DBProto.Database db;
    MessageExecutorTestImpl messageExecutorTestImpl;

    @Before
    public void setUp() throws Exception {
        // 删除test下所有文件
        File dir = new File(testContext.getConfig().getDbBaseFilePath());
        File[] files = dir.listFiles();
        for (File f: files){
            f.delete();
        }
        Map<String, DBProto.ValueObject> kvs = Maps.newHashMap();
        kvs.put("Hello", ValueObjects.newInstance("World"));
        kvs.put("Hi", ValueObjects.newInstance("World"));
        kvs.put("Haha", ValueObjects.newInstance("World"));
        db = DBProto.Database.newBuilder()
                .setVersion(1L)
                .putAllData(kvs)
                .build();
        messageExecutorTestImpl = new MessageExecutorTestImpl();
        testContext.setMemoryOperationExecutor(messageExecutorTestImpl);
    }

    /**
     * 测试
     * 1.能够正常地存盘
     * 2.存盘之后能够通知MessageExecutor
     * 3.存盘之后的文件能够再加载出来再读且与原始kvs一模一样
     * 4.检查是否发了callback消息
     * */
    @Test
    public void testSave() throws Exception {
        Proto.Request dbRequest = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerSave)
                .setInnerSaveRequest(Proto.InnerSaveRequest.newBuilder()
                        .setDb(db)
                        .setFilePath(DBUtils.genFilePath(testContext.getConfig().getDbBaseFilePath()))
                        .build())
                .build();
        testContext.getDbExecutor().submit(dbRequest);
        Thread.sleep(500);
        File dir = new File(testContext.getConfig().getDbBaseFilePath());
        File[] files = dir.listFiles();
        assertNotNull(files);
        assertEquals(1, files.length);
        File file = files[0];
        InputStream input = new FileInputStream(file);
        DBProto.Database newDb = DBProto.Database.parseFrom(input);
        assertEquals(3, newDb.getDataCount());
        assertEquals("World", newDb.getDataMap().get("Hello").getStringValue());
        assertEquals(1, messageExecutorTestImpl.requestList.size());
        Proto.Request request = messageExecutorTestImpl.requestList.get(0);
        assertEquals(Proto.MessageType.InnerSaveFinish, request.getMessageType());
        assertTrue(request.hasInnerSaveFinishRequest());
    }

    @After
    public void tearDown() throws Exception {
    }
}