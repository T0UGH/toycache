package com.t0ugh.server.storage;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
public class RewriteLogBuffer {
    private Map<String, List<Proto.Request>> bufferMap;

    public void delete(String key){
        bufferMap.remove(key);
    }

    public void put(String key, Proto.Request request){
        if(!bufferMap.containsKey(key)){
            bufferMap.put(key, new ArrayList<>());
        }
        bufferMap.get(key).add(request);
    }

    public List<Proto.Request> toList() {
        List<List<Proto.Request>> requests = Lists.newArrayList(bufferMap.values());
        return requests.stream().
                flatMap(Collection::stream).
                sorted((o1, o2) -> (int) (o1.getWriteId() - o2.getWriteId())).
                collect(Collectors.toList());
    }

    public void clear(){
        bufferMap.clear();
    }
}
