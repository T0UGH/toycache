package com.t0ugh.server.storage;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.utils.DBUtils;
import lombok.Builder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Builder
public class MemoryDBStorage implements Storage{

    private final Map<String, DBProto.ValueObject> kvs;

    public MemoryDBStorage() {
        kvs = Maps.newHashMap();
    }

    @Override
    public Map<String, DBProto.ValueObject> backdoor() {
        return kvs;
    }

    @Override
    public boolean exists(String key) {
        return kvs.containsKey(key);
    }

    @Override
    public String get(String key) throws ValueTypeNotMatchException {
        DBProto.ValueObject vo = kvs.get(key);
        if(Objects.isNull(vo)){
            return null;
        }
        if(!Objects.equals(DBProto.ValueType.ValueTypeString, vo.getValueType())){
            throw new ValueTypeNotMatchException();
        }
        return kvs.get(key).getStringValue();
    }

    @Override
    public void set(String key, String value) {
        DBProto.ValueObject vo = DBProto.ValueObject.newBuilder()
                .setValueType(DBProto.ValueType.ValueTypeString)
                .setStringValue(value)
                .build();
        kvs.put(key, vo);
    }

    @Override
    public boolean del(String key) {
        if (!kvs.containsKey(key)){
            return false;
        }
        kvs.remove(key);
        return true;
    }

    @Override
    public void writeToFile(String filePath) throws IOException {
        DBUtils.writeToFile(filePath, kvs);
    }

    @Override
    public void loadFromFile(String filePath) throws IOException {
        DBUtils.loadFromFile(filePath, kvs);
    }

    @Override
    public DBProto.Database toUnModifiableDB() {
        Map<String, DBProto.ValueObject> newKvs = Maps.newHashMap();
        kvs.forEach((k,  v) -> {
            try {
                newKvs.put(k, DBProto.ValueObject.parseFrom(v.toByteArray()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });
        return DBProto.Database.newBuilder().setVersion(1L).putAllKeyValues(newKvs).build();
    }
}
