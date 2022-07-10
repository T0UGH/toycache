package com.t0ugh.server.storage;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.Proto;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
public class RDBBuffer {
    private Map<String, List<Proto.Request>> bufferMap;
    private List<Proto.Request> expireList;

    public List<Proto.Request> toList() {
        List<List<Proto.Request>> requests = Lists.newArrayList(bufferMap.values());
        requests.add(expireList);
        return requests.stream().
                flatMap(Collection::stream).
                sorted((o1, o2) -> (int) (o1.getWriteId() - o2.getWriteId())).
                collect(Collectors.toList());
    }

    public void clear(){
        bufferMap.clear();
        expireList = Lists.newArrayList();
    }
}
