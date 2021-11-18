package com.t0ugh.server.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.DBProto;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
public class MemoryValueObject {
    private DBProto.ValueType valueType;
    private String stringValue;
    private Set<String> setValue;
    private List<String> listValue;
    private Map<String, String> mapValue;
    private MemorySortedSet sortedSetValue;

    public DBProto.ValueObject toValueObject(){
        DBProto.ValueObject.Builder vb = DBProto.ValueObject.newBuilder();
        vb.setValueType(valueType);
        switch (valueType){
            case ValueTypeString:
                vb.setStringValue(stringValue);
                break;
            case ValueTypeSet:
                vb.addAllSetValue(setValue);
                break;
            case ValueTypeList:
                vb.addAllListValue(listValue);
                break;
            case ValueTypeMap:
                vb.putAllMapValue(mapValue);
                break;
            case ValueTypeSortedSet:
                List<DBProto.ComparableString> cl = Lists.newArrayList();
                sortedSetValue.backdoorS().forEach(v -> {cl.add(v.toComparableString());});
                vb.addAllSortedSetValue(cl);
                break;
            default:
                break;
        }
        return vb.build();
    }

    public static MemoryValueObject newInstance(String value){
        return MemoryValueObject.builder()
                .valueType(DBProto.ValueType.ValueTypeString)
                .stringValue(value)
                .build();
    }

    public static MemoryValueObject newInstance(Set<String> value){
        return MemoryValueObject.builder()
                .valueType(DBProto.ValueType.ValueTypeSet)
                .setValue(value)
                .build();
    }

    public static MemoryValueObject newInstance(List<String> value) {
        return MemoryValueObject.builder()
                .valueType(DBProto.ValueType.ValueTypeSet)
                .listValue(Lists.newLinkedList(value))
                .build();
    }

    public static MemoryValueObject newInstance(Map<String, String> value) {
        return MemoryValueObject.builder()
                .valueType(DBProto.ValueType.ValueTypeMap)
                .mapValue(Maps.newHashMap(value))
                .build();
    }

    public static MemoryValueObject newInstance(MemorySortedSet value) {
        return MemoryValueObject.builder()
                .valueType(DBProto.ValueType.ValueTypeSortedSet)
                .sortedSetValue(value)
                .build();
    }

    public static MemoryValueObject fromValueObject(DBProto.ValueObject valueObject){
        MemoryValueObjectBuilder mb = MemoryValueObject.builder();
        mb.valueType(valueObject.getValueType());
        switch (valueObject.getValueType()){
            case ValueTypeString:
                mb.stringValue(valueObject.getStringValue());
                break;
            case ValueTypeSet:
                mb.setValue(Sets.newHashSet(valueObject.getSetValueList()));
                break;
            case ValueTypeList:
                mb.listValue(Lists.newArrayList(valueObject.getListValueList()));
                break;
            case ValueTypeMap:
                mb.mapValue(Maps.newHashMap(valueObject.getMapValueMap()));
                break;
            case ValueTypeSortedSet:
                MemorySortedSet memorySortedSet = new MemorySortedSet();
                memorySortedSet.addAll(Sets.newTreeSet(valueObject.getSortedSetValueList().stream()
                        .map(MemoryComparableString::parseFrom)
                        .collect(Collectors.toSet())));
                mb.sortedSetValue(memorySortedSet);
                break;
            default:
                break;
        }
        return mb.build();
    }
}
