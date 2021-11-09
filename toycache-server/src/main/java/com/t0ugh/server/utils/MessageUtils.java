package com.t0ugh.server.utils;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.storage.ExpireMap;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class MessageUtils {

    public static Proto.Response.Builder okBuilder() {
        return Proto.Response.newBuilder()
                .setResponseCode(Proto.ResponseCode.OK);
    }

    public static Proto.Response responseWithCode(Proto.ResponseCode code) {
        return Proto.Response.newBuilder()
                .setResponseCode(code).build();
    }

    /**
     * 检查请求中是否包含对应MessageType的子请求
     * */
    public static boolean containRequest(Proto.Request request) {
        try {
            Method hasXXXRequestMethod = request.getClass().getMethod("has"+ request.getMessageType().getValueDescriptor().getName() +"Request");
            return (boolean)hasXXXRequestMethod.invoke(request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("", e);
            return false;
        }
    }

    /**
     * 返回这个请求对应的Key
     * 如果key找到了就返回key本身
     * 如果找key途中报错或者请求中就没有key就返回
     * */
    public static Optional<String> getKeyFromRequest(Proto.Request request) {
        try {
            Class<?> clazz = Class.forName("com.t0ugh.sdk.proto.Proto." + request.getMessageType().getValueDescriptor().getName() +"Request");
            Method method = request.getClass().getMethod("get"+ request.getMessageType().getValueDescriptor().getName() +"Request");
            Object innerRequest = method.invoke(request);
            Method getKeyMethod = clazz.getMethod("getKey");
            String key = (String)getKeyMethod.invoke(innerRequest);
            if (Strings.isNullOrEmpty(key))
                return Optional.empty();
            return Optional.of(key);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
