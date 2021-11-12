package com.t0ugh.server.storage;

import com.google.common.collect.Maps;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;

import java.util.Map;
import java.util.Objects;

public class MemoryDBStorage implements Storage{

    private final Map<String, DBProto.ValueObject> map;

    public MemoryDBStorage() {
        map = Maps.newHashMap();
    }

    @Override
    public Map<String, DBProto.ValueObject> backdoor() {
        return map;
    }

    @Override
    public boolean exists(String key) {
        return map.containsKey(key);
    }

    @Override
    public String get(String key) throws ValueTypeNotMatchException {
        DBProto.ValueObject vo = map.get(key);
        if(Objects.isNull(vo)){
            return null;
        }
        if(!Objects.equals(DBProto.ValueType.ValueTypeString, vo.getValueType())){
            throw new ValueTypeNotMatchException();
        }
        return map.get(key).getStringValue();
    }

    @Override
    public void set(String key, String value) {
        DBProto.ValueObject vo = DBProto.ValueObject.newBuilder()
                .setValueType(DBProto.ValueType.ValueTypeString)
                .setStringValue(value)
                .build();
        map.put(key, vo);
    }

    @Override
    public boolean del(String key) {
        if (!map.containsKey(key)){
            return false;
        }
        map.remove(key);
        return true;
    }
}
