package com.t0ugh.server.rollbacker.key;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;

/**
 * 原先就有Expire的就恢复为老的Expire -> AbstractRollBacker中本来就包含这个逻辑
 * 原先没有Expire的直接删除这个新的Expire -> 这个逻辑需要在ExpireRollBacker中实现
 * */
public class ExpireRollBacker extends AbstractAllValueTypeRollBacker{

    private Long originalExpireTime = null;

    public ExpireRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        if (originalExpireTime == null) {
            getGlobalContext().getStorage().getExpireMap().remove(getKey());
        }
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        originalExpireTime = getGlobalContext().getStorage().getExpireMap().getOrDefault(getKey(), null);
        // 校验参数
        long expireTime = request.getExpireRequest().getExpireTime();
        if (expireTime <= 0) {
            throw new InvalidParamException();
        }
    }
}
