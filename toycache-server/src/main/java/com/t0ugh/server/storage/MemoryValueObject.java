package com.t0ugh.server.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.DBProto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

@Data
@Builder
public class MemoryValueObject {
    private DBProto.ValueType valueType;
    private String stringValue;
    private Set<String> setValue;
    private List<String> listValue;
    private Map<String, String> mapValue;
    private SortedSet<String> sortedSetValue;

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
            //todo: 这几个待实现
            case ValueTypeList:
                vb.addAllListValue(listValue);
                break;
            case ValueTypeMap:
                vb.putAllMapValue(mapValue);
                break;
            case ValueTypeSortedSet:
                throw new UnsupportedOperationException();
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
            //todo: 这几个待实现
            case ValueTypeList:
                mb.listValue(Lists.newArrayList(valueObject.getListValueList()));
                break;
            case ValueTypeMap:
                mb.mapValue(Maps.newHashMap(valueObject.getMapValueMap()));
                break;
            case ValueTypeSortedSet:
                throw new UnsupportedOperationException();
            default:
                break;
        }
        return mb.build();
    }
}
