package com.t0ugh.server.rollbacker.string;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.storage.MemoryValueObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@RollBackerAnnotation(messageType = Proto.MessageType.Set)
public class SetRollBacker implements RollBacker {

    @NonNull
    private GlobalContext globalContext;
    private String key;
    private MemoryValueObject originalValue;
    private Long originalExpireTime;

    @Override
    public void init(Proto.Request request) {
        Proto.DelRequest req = request.getDelRequest();
        key = req.getKey();
        originalValue = globalContext.getStorage().backdoor().getOrDefault(key, null);
        originalExpireTime = globalContext.getStorage().expireBackdoor().getOrDefault(key, null);
    }

    @Override
    public void rollBack() {
        if (!Objects.isNull(originalValue)){
            globalContext.getStorage().backdoor().put(key, originalValue);
        } else {
            globalContext.getStorage().del(key);
        }
        if (!Objects.isNull(originalExpireTime)){
            globalContext.getStorage().expireBackdoor().put(key, originalExpireTime);
        }
    }
}
