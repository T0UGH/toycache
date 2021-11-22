package com.t0ugh.server.rollbacker.key;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@RollBackerAnnotation(messageType = Proto.MessageType.Del)
public class DelRollBacker implements RollBacker {

    @NonNull
    private GlobalContext globalContext;
    private String key;
    private MemoryValueObject originalValue;
    private Long originalExpireTime;

    @Override
    public void beforeHandle(Proto.Request request) throws InvalidParamException {
        Proto.DelRequest req = request.getDelRequest();
        key = req.getKey();
        MessageUtils.assertStringNotNullOrEmpty(key);
        // 获取这个键对应的原来的MemoryValueObject, 如果是null, 就说明keyNotExists
        originalValue = globalContext.getStorage().backdoor().getOrDefault(key, null);
        // 获取这个键对应的原来的超时时间, 如果是null, 就说明要么这个key不存在要么没有通过这个key设置超时时间(永不超时)
        originalExpireTime = globalContext.getStorage().expireBackdoor().getOrDefault(key, null);
    }

    @Override
    public void rollBack() {
        // 如果originalValue存在, 就复原回去
        if (!Objects.isNull(originalValue)){
            globalContext.getStorage().backdoor().put(key, originalValue);
        }
        // 如果originalExpireTime存在, 就复原回去
        if (!Objects.isNull(originalExpireTime)){
            globalContext.getStorage().expireBackdoor().put(key, originalExpireTime);
        }
    }
}
