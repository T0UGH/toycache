package com.t0ugh.server.storage;

import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;
import com.t0ugh.sdk.proto.DBProto;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoryComparableString implements Comparable<MemoryComparableString>{
    private String stringValue;
    private double score;
    public static final String MAX_STRING = null;
    public static final String MIN_STRING = "";

    @Override
    public int compareTo(MemoryComparableString o) {
        int val = Doubles.compare(o.score, this.score);
        if(val != 0){
            return val;
        }
        if (stringValue == MAX_STRING) {
            return -1;
        }
        if (stringValue.equals(MIN_STRING)) {
            return 1;
        }
        if (o.getStringValue() == MAX_STRING) {
            return 1;
        }
        if (o.getStringValue().equals(MIN_STRING)) {
            return -1;
        }
        return stringValue.compareTo(o.getStringValue());
    }

    public DBProto.ComparableString toComparableString(){
        return DBProto.ComparableString.newBuilder().setStringValue(stringValue).setScore(score).build();
    }

    public static MemoryComparableString parseFrom(DBProto.ComparableString comparableString){
        return MemoryComparableString.builder()
                .stringValue(comparableString.getStringValue())
                .score(comparableString.getScore())
                .build();
    }

    @Override
    public int hashCode(){
        return stringValue.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if (Objects.isNull(obj) || !(obj instanceof MemoryComparableString)){
            return false;
        }

        if(Strings.isNullOrEmpty(stringValue)){
            return false;
        }

        if (Strings.isNullOrEmpty(((MemoryComparableString) obj).getStringValue())){
            return false;
        }
        return stringValue.equals(((MemoryComparableString) obj).getStringValue());
    }
}
