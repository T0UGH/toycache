package com.t0ugh.server.utils;

import com.t0ugh.sdk.proto.DBProto;

import java.io.*;
import java.util.Map;

public class DBUtils {
    public static void writeToFile(String filePath, Map<String, DBProto.ValueObject> kvs) throws IOException {
        DBProto.Database db = DBProto.Database.newBuilder().setVersion(1L).putAllData(kvs).build();
        OutputStream outputStream = new FileOutputStream(filePath);
        db.writeTo(outputStream);
    }

    public static void loadFromFile(String filePath, Map<String, DBProto.ValueObject> kvs) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        DBProto.Database db = DBProto.Database.parseFrom(inputStream);
        kvs.putAll(db.getDataMap());
    }

    public static void writeToFile(DBProto.Database database, String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);
        database.writeTo(outputStream);
    }

    public static DBProto.Database loadFromFile(String filePath) throws IOException{
        return DBProto.Database.parseFrom(new FileInputStream(filePath));
    }

    public static String genFilePath(String baseFilePath){
        return baseFilePath +"\\"+ System.currentTimeMillis() + ".tcdb";
    }
}
