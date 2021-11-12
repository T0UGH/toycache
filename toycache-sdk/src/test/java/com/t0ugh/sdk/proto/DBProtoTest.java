package com.t0ugh.sdk.proto;

import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DBProtoTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testValueTypeString() throws Exception {
        DBProto.Database db = DBProto.Database.newBuilder()
                .setVersion(1L)
                .putKeyValues("key", DBProto.ValueObject.newBuilder()
                        .setValueType(DBProto.ValueType.ValueTypeString)
                        .setStringValue("Hello")
                        .build())
                .build();
        String value = db.getKeyValuesMap().get("key").getStringValue();
        System.out.println(value);
    }

    @Test
    public void testValueTypeList() throws Exception {
        DBProto.Database db = DBProto.Database.newBuilder()
                .setVersion(1L)
                .putKeyValues("key", DBProto.ValueObject.newBuilder()
                        .setValueType(DBProto.ValueType.ValueTypeList)
                        .addListValue("Hello")
                        .addListValue("Hi")
                        .addListValue("Haha")
                        .build())
                .build();

        List<String> values = db.getKeyValuesMap().get("key").getListValueList();
        System.out.println(values.size());
        System.out.println(values.get(0));
    }

    @Test
    public void testValueTypeSet() throws Exception {
        DBProto.Database db = DBProto.Database.newBuilder()
                .setVersion(1L)
                .putKeyValues("key", DBProto.ValueObject.newBuilder()
                        .setValueType(DBProto.ValueType.ValueTypeSet)
                        .addSetValue("Hello")
                        .addSetValue("Hi")
                        .addSetValue("Haha")
                        .build())
                .build();


        Set<String> values = Sets.newHashSet(db.getKeyValuesMap().get("key").getListValueList());
        System.out.println(values.size());
        System.out.println(values.contains("Hello"));
    }

    // todo SortedSet这块不行
    // SortedSet 根据什么来排序
    @Test
    public void testValueTypeSortedSet() throws Exception {
        DBProto.Database db = DBProto.Database.newBuilder()
                .setVersion(1L)
                .putKeyValues("key", DBProto.ValueObject.newBuilder()
                        .setValueType(DBProto.ValueType.ValueTypeSortedSet)
                        .addSortedSetValue("Hi")
                        .build())
                .build();
    }

    @Test
    public void testValueTypeMap() throws Exception {
        DBProto.Database db = DBProto.Database.newBuilder()
                .setVersion(1L)
                .putKeyValues("key", DBProto.ValueObject.newBuilder()
                        .setValueType(DBProto.ValueType.ValueTypeSortedSet)
                        .putMapValue("Hi", "Hi")
                        .putMapValue("He", "He")
                        .build())
                .build();
        db.getKeyValuesMap().get("key").getMapValueMap().get("Hi");
    }
}