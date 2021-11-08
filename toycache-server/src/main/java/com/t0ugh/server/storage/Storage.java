package com.t0ugh.server.storage;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;

import java.util.Map;

public interface Storage {
    // todo 后门方法, 开发完记得删除
    Map<String, ValueObject> backdoor();
    boolean exists(String key);
    String get(String key) throws ValueTypeNotMatchException;

    void set(String key, String value);

    boolean del(String key);
}
