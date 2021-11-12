package com.t0ugh.server.utils;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.storage.Storage;

import java.io.*;

public class DBUtils {
    public static void writeToDB(DBProto.Database database, String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);
        database.writeTo(outputStream);
    }

    public static DBProto.Database loadFromDB(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        return DBProto.Database.parseFrom(inputStream);
    }
}
