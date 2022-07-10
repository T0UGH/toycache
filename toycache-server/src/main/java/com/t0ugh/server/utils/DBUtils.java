package com.t0ugh.server.utils;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.storage.MemoryValueObject;

import java.io.*;
import java.util.Map;

public class DBUtils {

    @Deprecated
    public static void loadFromFile(String filePath, Map<String, MemoryValueObject> kvs, Map<String, Long> expire) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        DBProto.Database db = DBProto.Database.parseFrom(inputStream);
        db.getDataMap().forEach((k, v) -> {
            kvs.put(k, MemoryValueObject.fromValueObject(v));
        });
        expire.putAll(db.getExpireMap());
        inputStream.close();
    }

    public static void loadRDBFromFile(String filePath, GlobalContext globalContext) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        try{
            // 先是ValueObject
            for(;;){
                try {
                    DBProto.KeyValue keyValue = DBProto.KeyValue.parseDelimitedFrom(inputStream);
                    globalContext.getStorage().backdoor().put(keyValue.getKey(), MemoryValueObject.fromValueObject(keyValue.getValueObject()));
                } catch (IOException e){
                    break;
                }
            }
            // 然后是request信息
            for(;;){
                try {
                    Proto.Request request = Proto.Request.parseDelimitedFrom(inputStream);
                    globalContext.getHandlerFactory().getHandler(request.getMessageType()).get().handle(request);
                } catch (IOException e){
                    break;
                }
            }
            // 最后加载meta信息
            DBProto.DatabaseMeta databaseMeta = DBProto.DatabaseMeta.parseDelimitedFrom(inputStream);
            globalContext.getGlobalState().getWriteCount().set(databaseMeta.getLastWriteId());
            globalContext.getGlobalState().setEpoch(databaseMeta.getLastEpoch());
            globalContext.getStorage().expireBackdoor().putAll(databaseMeta.getExpireMap());
        } finally {
            inputStream.close();
        }
    }

    public static void applyDb(DBProto.Database db, Map<String, MemoryValueObject> kvs, Map<String, Long> expire) {
        db.getDataMap().forEach((k, v) -> {
            kvs.put(k, MemoryValueObject.fromValueObject(v));
        });
        expire.putAll(db.getExpireMap());
    }

    public static void writeToFile(DBProto.Database database, String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);
        database.writeTo(outputStream);
        outputStream.close();
    }

    public static DBProto.Database loadFromFile(String filePath) throws IOException{
        InputStream inputStream = new FileInputStream(filePath);
        DBProto.Database db = DBProto.Database.parseFrom(inputStream);
        inputStream.close();
        return db;
    }

    public static String genFilePath(String baseFilePath){
        return baseFilePath +"\\"+ System.currentTimeMillis() + ".tcdb";
    }
}
