package com.t0ugh.server.storage;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.t0ugh.sdk.proto.Proto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestBuffer {
    private List<Proto.Request> buffer;
    private Map<Long, Proto.Request> bufferMap; // writeId -> request
    private int maxSize;

    public RequestBuffer(int maxSize){
        this.maxSize = maxSize;
        buffer = Lists.newLinkedList();
        bufferMap = Maps.newHashMap();
    }

    public void add(Proto.Request request){
        if (buffer.size() == maxSize){
            Proto.Request deleted = buffer.remove(0);
            bufferMap.remove(deleted.getWriteId());
        }
        buffer.add(request);
        bufferMap.put(request.getWriteId(), request);
    }

    public Optional<Proto.Request> get(long writeId){
        Proto.Request request = bufferMap.get(writeId);
        if (request == null) {
            return Optional.empty();
        }
        return Optional.of(request);
    }

    public boolean exists(long writeId) {
        return bufferMap.containsKey(writeId);
    }

    public long minWriteId(){
        if (buffer.size() == 0){
            return Long.MAX_VALUE;
        }
        return buffer.get(0).getWriteId();
    }
}
