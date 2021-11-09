package com.t0ugh.server.storage;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 实际的cache
 * */
public class MemoryStorage implements Storage {

    private final Map<String, ValueObject> map = new HashMap<>();;

    public Map<String, ValueObject> backdoor() {
        return this.map;
    }

    public boolean exists(String key) {
        return map.containsKey(key);
    }

    public String get(String key) throws ValueTypeNotMatchException {
        ValueObject vo = map.get(key);
        if (!Objects.equals(vo.getValueType(), ValueType.ValueTypeString)){
            throw new ValueTypeNotMatchException();
        }
        return vo.getStringObj();
    }

    @Override
    public void set(String key, String value) {
        ValueObject val = ValueObject.newStringObject(value);
        map.put(key, val);
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
