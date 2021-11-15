package com.t0ugh.server.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.utils.DBUtils;
import com.t0ugh.server.utils.OtherUtils;
import lombok.extern.slf4j.Slf4j;
import sun.swing.StringUIClientPropertyKey;

import javax.print.attribute.IntegerSyntax;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class MemoryDBStorage implements Storage{

    private final Map<String, MemoryValueObject> data;
    private final Map<String, Long> expire;

    public MemoryDBStorage() {
        data = Maps.newHashMap();
        expire = Maps.newHashMap();
    }

    @Override
    public Map<String, MemoryValueObject> backdoor() {
        return data;
    }

    @Override
    public Map<String, Long> expireBackdoor() {
        return expire;
    }

    @Override
    public boolean exists(String key) {
        return data.containsKey(key);
    }

    @Override
    public String get(String key) throws ValueTypeNotMatchException {
        MemoryValueObject vo = data.get(key);
        if(Objects.isNull(vo)){
            return null;
        }
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeString);
        return data.get(key).getStringValue();
    }

    @Override
    public void set(String key, String value) {
        MemoryValueObject vo = MemoryValueObject.newInstance(value);
        data.put(key, vo);
    }

    @Override
    public int sAdd(String key, Set<String> values) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            data.put(key, MemoryValueObject.newInstance(values));
            return values.size();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        return (int) values.stream().filter(v -> vo.getSetValue().add(v)).count();
    }

    @Override
    public int sCard(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        return vo.getSetValue().size();
    }

    @Override
    public boolean sIsMember(String key, String member) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return false;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        return false;
    }

    @Override
    public Set<String> sMembers(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Sets.newHashSet();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        return vo.getSetValue();
    }

    @Override
    public Set<String> sPop(String key, int count) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Sets.newHashSet();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        Set<String> setValue = vo.getSetValue();
        if (setValue.size() <= count){
            return setValue;
        }
        Set<String> res = OtherUtils.randomPick(count, setValue);
        setValue.removeAll(res);
        return res;
    }

    @Override
    public int sRem(String key, Set<String> members) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        Set<String> setValue = vo.getSetValue();
        return (int) setValue.stream().filter(v -> members.contains(v) && setValue.remove(v)).count();
    }

    @Override
    public Set<String> sRandMember(String key, int count) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Sets.newHashSet();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        Set<String> setValue = vo.getSetValue();
        if (setValue.size() <= count){
            return setValue;
        }
        Set<String> res = OtherUtils.randomPick(count, setValue);
        setValue.removeAll(res);
        return res;
    }

    @Override
    public boolean del(String key) {
        if (!data.containsKey(key)){
            return false;
        }
        data.remove(key);
        return true;
    }

    @Override
    public void loadFromFile(String filePath) throws IOException {
        DBUtils.loadFromFile(filePath, data, expire);
    }

    @Override
    public DBProto.Database toUnModifiableDB() {
        Map<String, DBProto.ValueObject> newKvs = Maps.newHashMap();
        Map<String, Long> newExpire = Maps.newHashMap();
        data.forEach((k, v) -> {
            try {
                newKvs.put(k, DBProto.ValueObject.parseFrom(v.toValueObject().toByteArray()));
            } catch (InvalidProtocolBufferException e) {
                log.error("", e);
            }
        });
        newExpire.putAll(expire);
        return DBProto.Database.newBuilder()
                .setVersion(1L)
                .putAllData(newKvs)
                .putAllExpire(newExpire)
                .build();
    }

    @Override
    public boolean isExpired(String key) {
        if (!expire.containsKey(key)){
            return false;
        }
        long expireTime = expire.get(key);
        return isTimeExpired(expireTime);
    }

    @Override
    public boolean delIfExpired(String key) {
        boolean isExpired = isExpired(key);
        if (isExpired) {
            expire.remove(key);
            data.remove(key);
        }
        return isExpired;
    }

    @Override
    public void setExpire(String key, long expireTime) {
        expire.put(key, expireTime);
    }

    @Override
    public int delExpires(int threshold) {
        List<String> expiredKeys = Lists.newArrayList();
        for (Map.Entry<String, Long> entry : expire.entrySet()){
            if (expiredKeys.size() >= threshold){
                break;
            }
            if(isTimeExpired(entry.getValue())){
                expiredKeys.add(entry.getKey());
            }
        }
        expiredKeys.forEach(expire::remove);
        expiredKeys.forEach(data::remove);
        return expiredKeys.size();
    }

    @Override
    public boolean delExpire(String key) {
        if (!expire.containsKey(key)){
            return false;
        }
        expire.remove(key);
        return true;
    }

    private static boolean isTimeExpired(long time){
        return time <= System.currentTimeMillis();
    }

    private void assertTypeMatch(MemoryValueObject vo, DBProto.ValueType valueType) throws ValueTypeNotMatchException {
        if(!Objects.equals(vo.getValueType(), valueType)){
            throw new ValueTypeNotMatchException();
        }
    }
}
