package com.t0ugh.server.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.utils.DBUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MemoryDBStorage implements Storage{

    private final Map<String, DBProto.ValueObject> data;
    private final Map<String, Long> expire;

    public MemoryDBStorage() {
        data = Maps.newHashMap();
        expire = Maps.newHashMap();
    }

    @Override
    public Map<String, DBProto.ValueObject> backdoor() {
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
        DBProto.ValueObject vo = data.get(key);
        if(Objects.isNull(vo)){
            return null;
        }
        if(!Objects.equals(DBProto.ValueType.ValueTypeString, vo.getValueType())){
            throw new ValueTypeNotMatchException();
        }
        return data.get(key).getStringValue();
    }

    @Override
    public void set(String key, String value) {
        DBProto.ValueObject vo = DBProto.ValueObject.newBuilder()
                .setValueType(DBProto.ValueType.ValueTypeString)
                .setStringValue(value)
                .build();
        data.put(key, vo);
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
    public void writeToFile(String filePath) throws IOException {
        DBUtils.writeToFile(filePath, data);
    }

    @Override
    public void loadFromFile(String filePath) throws IOException {
        DBUtils.loadFromFile(filePath, data);
    }

    @Override
    public DBProto.Database toUnModifiableDB() {
        Map<String, DBProto.ValueObject> newKvs = Maps.newHashMap();
        data.forEach((k, v) -> {
            try {
                newKvs.put(k, DBProto.ValueObject.parseFrom(v.toByteArray()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });
        return DBProto.Database.newBuilder().setVersion(1L).putAllData(newKvs).build();
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
}
