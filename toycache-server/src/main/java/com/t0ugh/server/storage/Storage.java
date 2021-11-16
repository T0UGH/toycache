package com.t0ugh.server.storage;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Storage {
    // todo 后门方法, 开发完记得删除
    Map<String, MemoryValueObject> backdoor();
    Map<String, Long> expireBackdoor();
    boolean exists(String key);
    String get(String key) throws ValueTypeNotMatchException;

    void set(String key, String value);

    int sAdd(String key, Set<String> values) throws ValueTypeNotMatchException;

    int sCard(String key) throws ValueTypeNotMatchException;

    boolean sIsMember(String key, String member) throws ValueTypeNotMatchException;

    Set<String> sMembers(String key) throws ValueTypeNotMatchException;

    Set<String> sPop(String key, int count) throws ValueTypeNotMatchException;

    int sRem(String key, Set<String> members) throws ValueTypeNotMatchException;

    Set<String> sRandMember(String key, int count) throws ValueTypeNotMatchException;

    int lPush(String key, List<String> value) throws ValueTypeNotMatchException;

    Optional<String> lIndex(String key, int index) throws ValueTypeNotMatchException;

    int lLen(String key) throws ValueTypeNotMatchException;

    Optional<String> lPop(String key) throws ValueTypeNotMatchException;

    List<String> lRange(String key, int start, int end) throws ValueTypeNotMatchException;

    boolean lTrim(String key, int start, int end) throws ValueTypeNotMatchException;

    boolean lSet(String key, int index, String newValue) throws ValueTypeNotMatchException;

    void hSet(String key, String field, String value) throws ValueTypeNotMatchException;

    boolean hExists(String key, String field) throws ValueTypeNotMatchException;

    Optional<String> hGet(String key, String field) throws ValueTypeNotMatchException;

    Map<String, String> hGetAll(String key) throws ValueTypeNotMatchException;

    Set<String> hKeys(String key) throws ValueTypeNotMatchException;

    int hLen(String key) throws ValueTypeNotMatchException;

    int hDel(String key, Set<String> fields) throws ValueTypeNotMatchException;

    boolean del(String key);

    void loadFromFile(String filePath) throws IOException;

    DBProto.Database toUnModifiableDB();

    boolean isExpired(String key);

    boolean delIfExpired(String key);

    void setExpire(String key, long expireTime);

    int delExpires(int threshold);

    boolean delExpire(String key);
}
