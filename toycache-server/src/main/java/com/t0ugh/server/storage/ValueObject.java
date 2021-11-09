package com.t0ugh.server.storage;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

@Data
@Builder
public class ValueObject {
    private ValueType valueType;
    private String stringObj;
    private Map<String, String> mapObj;
    private List<String> listObj;
    private Set<String> setObj;
    private SortedSet<String> sortedSetObj;

    public static ValueObject newStringObject(String str) {
        return ValueObject.builder().valueType(ValueType.ValueTypeString).stringObj(str).build();
    }

    public static ValueObject newMapObject(Map<String, String> map) {
        return ValueObject.builder().valueType(ValueType.ValueTypeMap).mapObj(map).build();
    }

    public static ValueObject newListObject(List<String> list) {
        return ValueObject.builder().valueType(ValueType.ValueTypeList).listObj(list).build();
    }

    public static ValueObject newSetObject(Set<String> set) {
        return ValueObject.builder().valueType(ValueType.ValueTypeSet).setObj(set).build();
    }

    public static ValueObject newSortedSetObject(SortedSet<String> set) {
        return ValueObject.builder().valueType(ValueType.ValueTypeSortedSet).sortedSetObj(set).build();
    }
}