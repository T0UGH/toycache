package com.t0ugh.server.rollbacker.key;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;

@RollBackerAnnotation(messageType = Proto.MessageType.Del)
public class DelRollBacker extends AbstractAllValueTypeRollBacker {

    private String key;
    private MemoryValueObject originalValue;
    private Long originalExpireTime;

    public DelRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {

    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.DelRequest req = request.getDelRequest();
        MessageUtils.assertStringNotNullOrEmpty(key);
        // 获取这个键对应的原来的MemoryValueObject, 如果是null, 就说明keyNotExists
        originalValue = getGlobalContext().getStorage().backdoor().getOrDefault(key, null);
    }
}
