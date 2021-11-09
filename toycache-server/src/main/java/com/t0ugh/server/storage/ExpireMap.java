package com.t0ugh.server.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpireMap {
    private final Map<String, Long> map;

    public ExpireMap() {
        this.map = new HashMap<>();
    }

    /**
     * 判断给定Key是否超时, 只是简单的判断超时, 不会删除超时的键
     * 如果键不存在返回false
     * */
    public boolean isExpired(String key){
        if (!map.containsKey(key)){
            return false;
        }
        long expireTime = map.get(key);
        return isTimeExpired(expireTime);
    }

    /**
     * 如果给定key超时就会被删除, 并且返回是否超时
     * */
    public boolean deleteIfExpired(String key) {
        boolean isExpired = isExpired(key);
        if (isExpired) {
            map.remove(key);
        }
        return isExpired;
    }

    public Map<String, Long> backdoor(){
        return map;
    }

    public void set(String key, long expireMs) {
        if (expireMs <= 0) {
            throw new IllegalArgumentException();
        }
        // todo 这里的加法会不会发生逸出
        long expireTime = expireMs + System.currentTimeMillis();
        map.put(key, expireTime);
    }

    /**
     * 删除超时的键并且返回删除了的键
     * */
    public List<String> deleteExpires(int limit) {
        List<String> expiredKeys = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()){
            if (expiredKeys.size() >= limit){
                break;
            }
            if(isTimeExpired(entry.getValue())){
                expiredKeys.add(entry.getKey());
            }
        }
        expiredKeys.forEach(map::remove);
        return expiredKeys;
    }

    public boolean del(String key) {
        if (!map.containsKey(key)){
            return false;
        }
        map.remove(key);
        return true;
    }

    /**
     * todo: 这样实现超时是否好
     * */
    private static boolean isTimeExpired(long time){
        return time >= System.currentTimeMillis();
    }
}
