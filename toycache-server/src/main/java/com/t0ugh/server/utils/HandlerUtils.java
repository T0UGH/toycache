package com.t0ugh.server.utils;


import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.HandlerFactory;

import java.lang.reflect.Field;
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

    public static boolean hExistsWithoutCheck(String key, String field, GlobalContext globalContext){
        try {
            return globalContext.getStorage().hExists(key, field);
        } catch (ValueTypeNotMatchException e) {
            throw new UnsupportedOperationException("uncheck valueType");
        }
    }

    public static Optional<String> hGetWithoutCheck(String key, String field, GlobalContext globalContext){
        try {
            return globalContext.getStorage().hGet(key, field);
        } catch (ValueTypeNotMatchException e) {
            throw new UnsupportedOperationException("uncheck valueType");
        }
    }
}
