package com.t0ugh.server.storage;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Storage {
    // todo 后门方法, 开发完记得删除
    Map<String, DBProto.ValueObject> backdoor();
    Map<String, Long> expireBackdoor();
    boolean exists(String key);
    String get(String key) throws ValueTypeNotMatchException;

    void set(String key, String value);

    boolean del(String key);

    void loadFromFile(String filePath) throws IOException;

    DBProto.Database toUnModifiableDB();

    boolean isExpired(String key);

    boolean delIfExpired(String key);

    void setExpire(String key, long expireTime);

    int delExpires(int threshold);

    boolean delExpire(String key);
}
