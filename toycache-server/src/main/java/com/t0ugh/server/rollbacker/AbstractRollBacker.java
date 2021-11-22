package com.t0ugh.server.rollbacker;

import com.google.common.base.Strings;
import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.MessageUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractRollBacker implements RollBacker{

    @Getter
    private final GlobalContext globalContext;

    private boolean keyExists;
    private String key;
    private Long originalExpireTime = null;

    public AbstractRollBacker(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public void beforeHandle(Proto.Request request) throws Exception{
        // 首先看看Key是否Exists
        String key = MessageUtils.getKeyFromRequest(request).orElseThrow(InvalidParamException::new);
        keyExists = globalContext.getStorage().exists(key);
        // 后面惰性检查删除的时候可能会把expireTime删掉, Rollback会把它复原(就像从来没有执行过这个请求一样, 即使它确实过期了)
        originalExpireTime = globalContext.getStorage().expireBackdoor().getOrDefault(key, null);
        // 如果key存在再执行子类的beforeHandle
        if (keyExists){
            doBeforeHandle(request);
        }

    }

    @Override
    public void rollBack() {

        try {
            // 请求里不可能没传key
            // 如果原先不存在这个key, 那么应该清除key
            if (!keyExists){
                globalContext.getStorage().del(key);
                return;
            }
            // 如果原先这个key有一个超时时间, 复原它
            if (!Objects.isNull(originalExpireTime)){
                globalContext.getStorage().expireBackdoor().put(key, originalExpireTime);
            }
            doRollBack();
        } catch (Exception e) {
            // 讲道理回滚的时候不会抛出异常，如果抛出了异常应该就是BUG, 因此直接打到日志里就行了
            log.error("ToyCache occurred exception: ", e);
        }
    }

    public String getKey(){
        return key;
    }

    public abstract void doRollBack() throws Exception;
    public abstract void doBeforeHandle(Proto.Request request) throws Exception;
}
