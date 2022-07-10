package com.t0ugh.server.storage;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;

import java.io.IOException;
import java.util.*;

public interface Storage {
    // todo 后门方法, 开发完记得删除
    Map<String, MemoryValueObject> backdoor();

    Map<String, Long> expireBackdoor();

    boolean exists(String key);

    DBProto.ValueType getValueType(String key);

    Optional<String> get(String key) throws ValueTypeNotMatchException;

    void set(String key, String value) throws ValueTypeNotMatchException;

    int sAdd(String key, Set<String> values) throws ValueTypeNotMatchException;

    int sCard(String key) throws ValueTypeNotMatchException;

    boolean sIsMember(String key, String member) throws ValueTypeNotMatchException;

    Set<String> sMembers(String key) throws ValueTypeNotMatchException;

    Set<String> sPop(String key, int count) throws ValueTypeNotMatchException;

    int sRem(String key, Set<String> members) throws ValueTypeNotMatchException;

    Set<String> sRandMember(String key, int count) throws ValueTypeNotMatchException;

    int lPush(String key, List<String> value) throws ValueTypeNotMatchException;

    int rPush(String key, List<String> value) throws ValueTypeNotMatchException;


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

    int zAdd(String key, NavigableSet<MemoryComparableString> members) throws ValueTypeNotMatchException;

    int zCard(String key) throws ValueTypeNotMatchException;

    int zCount(String key, double min, double max) throws ValueTypeNotMatchException;

    Optional<Integer> zRank(String key, String member) throws ValueTypeNotMatchException;

    NavigableSet<MemoryComparableString> zRange(String key, int start, int end) throws ValueTypeNotMatchException;

    NavigableSet<MemoryComparableString> zRangeByScore(String key, double min, double max) throws ValueTypeNotMatchException;

    int zRem(String key, Set<String> members) throws ValueTypeNotMatchException;

    boolean zExists(String key, String strValue) throws ValueTypeNotMatchException;

    Optional<MemoryComparableString> zGet(String key, String strValue) throws ValueTypeNotMatchException;

    boolean del(String key);

    @Deprecated
    void loadFromFile(String filePath) throws IOException;

    void applyDb(DBProto.Database db);

    DBProto.Database toUnModifiableDB(long lastWriteId, long lastEpoch);

    boolean isExpired(String key);

    boolean delIfExpired(String key);

    void setExpire(String key, long expireTime);

    int delExpires(int threshold);

    boolean delExpire(String key);

    Optional<DBProto.KeyValue> cloneValue(String key);

    Set<String> keys();

    DBProto.DatabaseMeta getDatabaseMeta(long lastWriteId, long lastEpoch);
}
