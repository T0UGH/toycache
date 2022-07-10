package com.t0ugh.server.storage;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.protobuf.InvalidProtocolBufferException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.utils.DBUtils;
import com.t0ugh.server.utils.StorageUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class MemoryStorage implements Storage{

    private final Map<String, MemoryValueObject> data;
    private final Map<String, Long> expire;

    public MemoryStorage() {
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
    public DBProto.ValueType getValueType(String key) {
        if (data.containsKey(key)){
            return data.get(key).getValueType();
        }
        return DBProto.ValueType.ValueTypeInvalid;
    }

    @Override
    public Optional<String> get(String key) throws ValueTypeNotMatchException {
        MemoryValueObject vo = data.get(key);
        if(Objects.isNull(vo)){
            return Optional.empty();
        }
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeString);
        String value = data.get(key).getStringValue();
        if(Strings.isNullOrEmpty(value)){
            return Optional.empty();
        }
        return Optional.of(value);
    }

    @Override
    public void set(String key, String value)  throws ValueTypeNotMatchException{
        if (data.containsKey(key)){
            MemoryValueObject vo = data.get(key);
            assertTypeMatch(vo, DBProto.ValueType.ValueTypeString);
        }
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
        return vo.getSetValue().contains(member);
    }

    @Override
    public Set<String> sMembers(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Sets.newHashSet();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSet);
        return Sets.newHashSet(vo.getSetValue());
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
            Set<String> res = Sets.newHashSet(setValue);
            setValue.clear();
            return res;
        }
        Set<String> res = StorageUtils.randomPick(count, setValue);
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
        int count = Sets.intersection(setValue, members).size();
        setValue.removeIf(members::contains);
        return count;
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
        Set<String> res = StorageUtils.randomPick(count, setValue);
        setValue.removeAll(res);
        return res;
    }

    @Override
    public int lPush(String key, List<String> values) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            data.put(key, MemoryValueObject.newInstance(values));
            return values.size();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        List<String> listValue = vo.getListValue();
        listValue.addAll(0, values);
        return listValue.size();
    }

    @Override
    public int rPush(String key, List<String> values) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            data.put(key, MemoryValueObject.newInstance(values));
            return values.size();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        List<String> listValue = vo.getListValue();
        listValue.addAll(values);
        return listValue.size();
    }

    @Override
    public Optional<String> lIndex(String key, int index) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Optional.empty();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        List<String> listValue = vo.getListValue();
        try{
            int actualIndex = StorageUtils.assertAndConvertIndex(index, listValue.size());
            String val = listValue.get(actualIndex);
            return Optional.of(val);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public int lLen(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        return vo.getListValue().size();
    }

    @Override
    public Optional<String> lPop(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Optional.empty();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        List<String> listValue = vo.getListValue();
        if(listValue.size() < 1){
            return Optional.empty();
        }
        return Optional.of(listValue.remove(0));
    }

    @Override
    public List<String> lRange(String key, int start, int end) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Lists.newArrayList();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        List<String> listValue = vo.getListValue();
        try{
            int actualStart = StorageUtils.assertAndConvertIndex(start, listValue.size());
            int actualEnd = StorageUtils.assertAndConvertEndIndex(end, listValue.size());
            if (actualStart > actualEnd){
                return Lists.newArrayList();
            }
            List<String> res = Lists.newArrayList();
            int i = 0;
            for (String val: listValue) {
                if (i >= actualStart && i <= actualEnd){
                    res.add(val);
                }
                if (i > actualEnd){
                    break;
                }
                i ++;
            }
            return res;
        } catch (IndexOutOfBoundsException e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public boolean lTrim(String key, int start, int end) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return false;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        List<String> listValue = vo.getListValue();
        try{
            int actualStart = StorageUtils.assertAndConvertIndex(start, listValue.size());
            int actualEnd = StorageUtils.assertAndConvertEndIndex(end, listValue.size());
            if (actualStart > actualEnd){
                return false;
            }
            List<String> newListValue = Lists.newArrayList();
            int i = 0;
            for (String val: listValue) {
                if (i >= actualStart && i <= actualEnd){
                    newListValue.add(val);
                }
                if (i > actualEnd){
                    break;
                }
                i ++;
            }
            data.put(key, MemoryValueObject.newInstance(newListValue));
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public boolean lSet(String key, int index, String newValue) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return false;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeList);
        List<String> listValue = vo.getListValue();
        try{
            int actualIndex = StorageUtils.assertAndConvertIndex(index, listValue.size());
            String val = listValue.set(actualIndex, newValue);
            return true;
        // 如果发生了数组越界, 就直接返回false
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public void hSet(String key, String field, String value) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            Map<String, String> map = Maps.newHashMap();
            map.put(field, value);
            data.put(key, MemoryValueObject.newInstance(map));
            return;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeMap);
        vo.getMapValue().put(field, value);
        return;
    }

    @Override
    public boolean hExists(String key, String field) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return false;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeMap);
        Map<String, String> mapValue = vo.getMapValue();
        return mapValue.containsKey(field);
    }

    @Override
    public Optional<String> hGet(String key, String field) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Optional.empty();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeMap);
        Map<String, String> mapValue = vo.getMapValue();
        if(!mapValue.containsKey(field)){
            return Optional.empty();
        }
        return Optional.of(mapValue.get(field));
    }

    @Override
    public Map<String, String> hGetAll(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Maps.newHashMap();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeMap);
        Map<String, String> mapValue = vo.getMapValue();
        return Maps.newHashMap(mapValue);
    }

    @Override
    public Set<String> hKeys(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Sets.newHashSet();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeMap);
        Map<String, String> mapValue = vo.getMapValue();
        return mapValue.keySet();
    }

    @Override
    public int hLen(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeMap);
        Map<String, String> mapValue = vo.getMapValue();
        return mapValue.size();
    }

    @Override
    public int hDel(String key, Set<String> fields) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeMap);
        Map<String, String> mapValue = vo.getMapValue();
        return (int) fields.stream().filter(k -> !Objects.isNull(mapValue.remove(k))).count();
    }

    @Override
    public int zAdd(String key, NavigableSet<MemoryComparableString> members) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            MemorySortedSet newSortedSet = new MemorySortedSet();
            newSortedSet.addAll(members);
            data.put(key, MemoryValueObject.newInstance(newSortedSet));
            return members.size();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        MemorySortedSet sortedSet = vo.getSortedSetValue();
        return sortedSet.addAll(members);
    }

    @Override
    public int zCard(String key) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        MemorySortedSet sortedSetValue = vo.getSortedSetValue();
        return sortedSetValue.card();
    }

    @Override
    public int zCount(String key, double min, double max) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        MemorySortedSet sortedSetValue = vo.getSortedSetValue();
        return sortedSetValue.count(min, max);
    }

    @Override
    public Optional<Integer> zRank(String key, String member) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Optional.empty();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        MemorySortedSet sortedSetValue = vo.getSortedSetValue();
        return sortedSetValue.rank(member);
    }

    @Override
    public NavigableSet<MemoryComparableString> zRange(String key, int start, int end) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Sets.newTreeSet();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        MemorySortedSet sortedSetValue = vo.getSortedSetValue();
        return sortedSetValue.range(start, end);
    }

    @Override
    public NavigableSet<MemoryComparableString> zRangeByScore(String key, double min, double max) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Sets.newTreeSet();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        MemorySortedSet sortedSetValue = vo.getSortedSetValue();
        return sortedSetValue.rangeByScore(min, max);
    }

    @Override
    public int zRem(String key, Set<String> members) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return 0;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        MemorySortedSet sortedSetValue = vo.getSortedSetValue();
        return sortedSetValue.removeAll(members);
    }

    @Override
    public boolean zExists(String key, String strValue) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return false;
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        return vo.getSortedSetValue().exists(strValue);
    }

    @Override
    public Optional<MemoryComparableString> zGet(String key, String strValue) throws ValueTypeNotMatchException {
        if (!data.containsKey(key)){
            return Optional.empty();
        }
        MemoryValueObject vo = data.get(key);
        assertTypeMatch(vo, DBProto.ValueType.ValueTypeSortedSet);
        return vo.getSortedSetValue().get(strValue);
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
    @Deprecated
    public void loadFromFile(String filePath) throws IOException {
        DBUtils.loadFromFile(filePath, data, expire);
    }

    @Override
    public void applyDb(DBProto.Database db) {
        DBUtils.applyDb(db, data, expire);
    }

    @Override
    public DBProto.Database toUnModifiableDB(long lastWriteId, long lastEpoch) {
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
                .setLastWriteId(lastWriteId)
                .setLastEpoch(lastEpoch)
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

    @Override
    public Optional<DBProto.KeyValue> cloneValue(String key) {
        if (!data.containsKey(key)){
            return Optional.empty();
        }
        return Optional.of(DBProto.KeyValue.newBuilder().setKey(key).setValueObject(data.get(key).toValueObject()).build());
    }

    @Override
    public Set<String> keys() {
        return Sets.newHashSet(data.keySet());
    }

    @Override
    public DBProto.DatabaseMeta getDatabaseMeta(long lastWriteId, long lastEpoch) {
        Map<String, Long> newExpire = Maps.newHashMap();
        newExpire.putAll(expire);
        return DBProto.DatabaseMeta.newBuilder()
                .setLastWriteId(lastWriteId)
                .setLastEpoch(lastEpoch)
                .putAllExpire(newExpire)
                .build();
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
