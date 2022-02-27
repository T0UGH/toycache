package com.t0ugh.server.rollbacker;

import com.google.common.base.Strings;
import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.MessageUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

/**
 * 此抽象类会负责检查key是否存在, 检查ValueType是否match, 并且会保存和复原expireTime
 * */
@Slf4j
public abstract class AbstractRollBacker implements RollBacker{

    @Getter
    private final GlobalContext globalContext;

    private boolean keyExists;
    private String key;
    private Long originalExpireTime = null;
    @Getter
    private Proto.Request request;

    private boolean exceptionOccur;

    private boolean valueTypeMatch;

    public AbstractRollBacker(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }


    @Override
    public void beforeHandle(Proto.Request request){
        try{
            this.request = request;
            // 首先看看Key是否Exists
            key = MessageUtils.getKeyFromRequest(request).orElseThrow(InvalidParamException::new);
            keyExists = globalContext.getStorage().exists(key);
            // 后面惰性检查删除的时候可能会把expireTime删掉, Rollback会把它复原(就像从来没有执行过这个请求一样, 即使它确实过期了)
            originalExpireTime = globalContext.getStorage().expireBackdoor().getOrDefault(key, null);
            // !!! 如果key存在并且key的valueType是匹配的, 才会再执行子类的beforeHandle
            if (keyExists){
                // 如果子类返回的是ValueTypeAll，就说明不管什么ValueType, 子类都能处理，因此不需要进行检查
                if (Objects.equals(DBProto.ValueType.ValueTypeAll, getValueType())){
                    valueTypeMatch = true;
                } else {
                    valueTypeMatch = Objects.equals(globalContext.getStorage().getValueType(key), getValueType());
                }
                if (valueTypeMatch)
                    doBeforeHandle(request);
            }
        } catch (Exception e){
            exceptionOccur = true;
        }
    }

    @Override
    public void rollBack() {

        try {
            // 如果beforeHandle中出现了异常, rollBack直接return即可
            // 因为如果beforeHandle出现了ValueTypeNotMatchException或者InvalidParamException, 那么handler.handle()必定还会出现这些异常
            if (exceptionOccur) {
                return;
            }
            // 如果原先不存在这个key, 那么应该清除key
            if (!keyExists){
                globalContext.getStorage().del(key);
                return;
            }
            // 如果原先这个key有一个超时时间, 复原它
            if (!Objects.isNull(originalExpireTime)){
                globalContext.getStorage().expireBackdoor().put(key, originalExpireTime);
            }
            // 如果原先存在key，但是valueType不match, 那么就不执行回滚了
            if (!valueTypeMatch){
                return;
            }
            // 只有原先key就存在,并且valueType匹配时，才会执行回滚函数
            doRollBack();
        } catch (Exception e) {
            // 原则上回滚的时候不会抛出异常，如果抛出了异常应该就是BUG, 因此直接打到日志里就行了
            log.error("RollBacker occurred exception: ", e);
        }
    }

    public String getKey(){
        return key;
    }

    /**
     * doRollBack()会在与该请求同属于一个事务的后面请求出现错误时被调用，以使用RollBacker中保存的信息来恢复系统状态
     * 此方法只需要考虑key存在并且ValueTypeMatch时的情况即可
     * */
    public abstract void doRollBack() throws Exception;
    /**
     * doBeforeHandle会在handler.handle()执行之前被调用, 用于将回滚所需的足够信息存入RollBacker中
     * 此方法只需要考虑key存在并且ValueTypeMatch时的情况即可
     * 此方法可以抛出受查异常，如果抛出受查异常则doRollBack()方法不会执行
     * */
    public abstract void doBeforeHandle(Proto.Request request) throws Exception;
    /**
     * 子类需要使用此方法来提供子类能够处理的ValueType
     * */
    public abstract DBProto.ValueType getValueType();
}
