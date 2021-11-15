package com.t0ugh.server.utils;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.storage.MemoryValueObject;

import java.io.*;
import java.util.Map;

public class DBUtils {

    public static void loadFromFile(String filePath, Map<String, MemoryValueObject> kvs, Map<String, Long> expire) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        DBProto.Database db = DBProto.Database.parseFrom(inputStream);
        db.getDataMap().forEach((k, v) -> {
            kvs.put(k, MemoryValueObject.fromValueObject(v));
        });
        expire.putAll(db.getExpireMap());
        inputStream.close();
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
