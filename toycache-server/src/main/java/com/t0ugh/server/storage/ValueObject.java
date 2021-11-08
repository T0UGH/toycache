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
}
