package com.t0ugh.server.utils;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class WriteLogUtils {
    public static List<Proto.Request> databaseToRequests(DBProto.Database database){
        //首先将data全部转换
        List<Proto.Request> requests = Lists.newArrayList();
        Map<String, DBProto.ValueObject> data = database.getDataMap();
        data.forEach((k, v) -> {
            switch (v.getValueType()){
                case ValueTypeString:
                    requests.add(MessageUtils.newSetRequest(k, v.getStringValue()));
                    break;
                //todo:下边这四种待实现
                case ValueTypeList:
                case ValueTypeMap:
                case ValueTypeSet:
                case ValueTypeSortedSet:
                    throw new UnsupportedOperationException();
                default:
                    break;
            }
        });
        //其次将expire全部转换
        Map<String, Long> expire = database.getExpireMap();
        expire.forEach((k, v) -> {
            requests.add(MessageUtils.newExpireRequest(k, v));
        });
        return requests;
    }

    public static OutputStream clearOutputStream(OutputStream old, String baseFilePath) throws IOException, InterruptedException {
        old.close();
        File file = new File(getWriteLogFilePath(baseFilePath));
        file.delete();
        OutputStream newOut = new FileOutputStream(getWriteLogFilePath(baseFilePath));
        return newOut;
    }

    public static String getWriteLogFilePath(String baseFilePath){
        return baseFilePath +"\\"+ "\\writeLog.tcwlog";
    }
}
