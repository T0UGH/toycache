package com.t0ugh.server.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;

public class StorageUtils {


    public static int assertAndConvertIndex(int indexMayNegative, int size) {
        int converted = indexMayNegative >= 0? indexMayNegative: size + indexMayNegative;
        if ( converted < 0 || converted >= size)
            throw new IndexOutOfBoundsException();
        return converted;
    }

    public static int assertAndConvertEndIndex(int endIndexMayNegative, int size) {
        int converted = endIndexMayNegative >= 0? endIndexMayNegative: size + endIndexMayNegative;
        if ( converted < 0)
            throw new IndexOutOfBoundsException();
        if (converted >= size){
            return size - 1;
        }
        return converted;
    }


    public static Set<String> randomPick(int num, Set<String> all){
        int size = all.size();
        if (num > size){
            return Sets.newHashSet(all);
        }
        if(num < 0){
            throw new RuntimeException("num or maxValue must be greater than zero");
        }
        List<String> allList = Lists.newArrayList(all);
        Set<String> res = Sets.newHashSet();
        Random random = new Random(System.currentTimeMillis());
        for(int i=0; i<num; i++){
            int index =  random.nextInt(size - i);
            res.add(allList.get(index));
            int lastIndex = size-i-1;
            if(index != lastIndex){
                allList.set(index, allList.get(lastIndex));
            }
        }
        return res;
    }
}
