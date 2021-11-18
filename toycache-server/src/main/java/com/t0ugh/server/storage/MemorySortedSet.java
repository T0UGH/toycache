package com.t0ugh.server.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.t0ugh.server.utils.StorageUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MemorySortedSet {
    private final Map<String, MemoryComparableString> m;
    private final NavigableSet<MemoryComparableString> s;

    public MemorySortedSet(){
        m = Maps.newHashMap();
        s = Sets.newTreeSet();
    }

    public boolean add(MemoryComparableString item){
        boolean contain = m.containsKey(item.getStringValue());
        if (contain) {
            MemoryComparableString old = m.get(item.getStringValue());
            s.remove(old);
        }
        m.put(item.getStringValue(), item);
        s.add(item);
        return !contain;
    }

    public int addAll(List<MemoryComparableString> ns){
        return (int) ns.stream().filter(this::add).count();
    }

    public int addAll(Set<MemoryComparableString> ns){
        return (int) ns.stream().filter(this::add).count();
    }

    public int removeAll(Set<String> ss){
        return (int) ss.stream().filter(this::remove).count();
    }

    public boolean remove(String stringValue) {
        if(!m.containsKey(stringValue)){
            return false;
        }
        s.remove(m.get(stringValue));
        m.remove(stringValue);
        return true;
    }

    public Optional<Integer> rank(String stringValue) {
        if(!m.containsKey(stringValue)){
            return Optional.empty();
        }
        return Optional.of(s.headSet(m.get(stringValue), false).size());
    }

    public int count(double min, double max) {
        if (min > max) {
            return 0;
        }
        return s.subSet(new MemoryComparableString(MemoryComparableString.MAX_STRING, max),
                new MemoryComparableString(MemoryComparableString.MIN_STRING, min)).size();
    }

    public NavigableSet<MemoryComparableString> range(int start, int end) {
        try {
            int actualStart = StorageUtils.assertAndConvertIndex(start, s.size());
            int actualEnd = StorageUtils.assertAndConvertEndIndex(end, s.size());
            if (actualStart > actualEnd){
                return Sets.newTreeSet();
            }
            return s.stream().skip(actualStart).limit(actualEnd + 1 - actualStart).collect(Collectors.toCollection(Sets::newTreeSet));
        } catch (IndexOutOfBoundsException e){
            return Sets.newTreeSet();
        }

    }

    public NavigableSet<MemoryComparableString> rangeByScore(double min, double max) {
        if (min > max) {
            return Sets.newTreeSet();
        }
        return (NavigableSet<MemoryComparableString>)s.subSet(new MemoryComparableString(MemoryComparableString.MAX_STRING, max),
                new MemoryComparableString(MemoryComparableString.MIN_STRING, min));
    }

    public int card(){
        return m.size();
    }

    public Optional<Double> score(String stringValue) {
        if(!m.containsKey(stringValue)){
            return Optional.empty();
        }
        return Optional.of(m.get(stringValue).getScore());
    }


    public Map<String, MemoryComparableString> backdoorM(){
        return m;
    }

    public NavigableSet<MemoryComparableString> backdoorS(){
        return s;
    }

}
