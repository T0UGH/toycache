package com.t0ugh.server.utils;


import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.HandlerFactory;

import java.util.Optional;

public class HandlerUtils {

    /**
     * 根据具体Handler上的 HandlerAnnotation注解 {@link com.t0ugh.server.handler.HandlerAnnotation()}的 checkExpire 属性
     * 判断一个MessageType是否需要进行键过期判断
     * */
    public static boolean needCheckExpire(Proto.MessageType messageType, HandlerFactory handlerFactory) {
        Optional<Handler> op = handlerFactory.getHandler(messageType);
        return op.map(handler -> handler.getClass().getAnnotation(HandlerAnnotation.class).checkExpire()).orElse(false);
    }

    /**
     * 如果键超时，就清除storage和ExpireMap中对应的kv对
     * @return 返回键是否超时, 超时返回true, 没超时或不存在返回false
     * */
    public static boolean clearStorageAndExpireMapIfExpired(String key, GlobalContext globalContext) {
        boolean isExpired = globalContext.getExpireMap().deleteIfExpired(key);
        if (isExpired) {
            globalContext.getStorage().del(key);
        }
        return isExpired;
    }
}
