package com.t0ugh.server.utils;

import com.t0ugh.sdk.proto.DBProto;

import java.io.*;
import java.util.Map;

public class DBUtils {

    public static void loadFromFile(String filePath, Map<String, DBProto.ValueObject> kvs, Map<String, Long> expire) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        DBProto.Database db = DBProto.Database.parseFrom(inputStream);
        kvs.putAll(db.getDataMap());
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
