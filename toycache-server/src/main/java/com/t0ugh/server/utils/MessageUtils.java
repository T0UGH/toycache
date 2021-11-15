package com.t0ugh.server.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.HandlerFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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


    public static boolean isWriteRequest(Proto.MessageType messageType, HandlerFactory handlerFactory){
        Optional<Handler> op = handlerFactory.getHandler(messageType);
        return op.map(handler -> handler.getClass().getAnnotation(HandlerAnnotation.class).isWrite()).orElse(false);
    }

    /**
     * 检查请求中是否包含对应MessageType的子请求
     * */
    public static boolean containRequest(Proto.Request request) {
        try {
            if(Objects.equals(Proto.MessageType.Invalid, request.getMessageType())){
                return false;
            }
            Method hasXXXRequestMethod = request.getClass().getMethod("has"+ request.getMessageType().getValueDescriptor().getName() +"Request");
            return (boolean)hasXXXRequestMethod.invoke(request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("", e);
            return false;
        }
    }

    /**
     * 返回这个请求对应的Key
     * 1. 如果key找到了就返回key本身
     * 2. 如果这个请求本身就没有key,就返回Optinal.empty()
     * 3. 如果如果发现找到的Key是""或null就抛出异常
     * 如果找key途中报错或者请求中就没有key就返回
     * */

    public static Optional<String> getKeyFromRequest(Proto.Request request) throws InvalidParamException {
        try {
            String className = "com.t0ugh.sdk.proto.Proto$" + request.getMessageType().getValueDescriptor().getName() +"Request";
            Class<?> clazz = Class.forName(className);
            Method method = request.getClass().getMethod("get"+ request.getMessageType().getValueDescriptor().getName() +"Request");
            Object innerRequest = method.invoke(request);
            Method getKeyMethod = clazz.getMethod("getKey");
            String key = (String)getKeyMethod.invoke(innerRequest);
            if (Strings.isNullOrEmpty(key))
                throw new InvalidParamException();
            return Optional.of(key);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            log.error("", e);
            return Optional.empty();
        }
    }


    public static Proto.Request newInnerClearExpireRequest(){
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerClearExpire)
                .setInnerClearExpireRequest(Proto.InnerClearExpireRequest.newBuilder()).build();
    }

    public static Proto.Request newStartSaveRequest(){
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Save)
                .setSaveRequest(Proto.SaveRequest.newBuilder())
                .build();
    }

    public static Proto.Request newSetRequest(String key, String value){
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Set)
                .setSetRequest(Proto.SetRequest.newBuilder().setKey(key).setValue(value))
                .build();
    }

    public static Proto.Request newExpireRequest(String key, long expireTime){
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Expire)
                .setExpireRequest(Proto.ExpireRequest.newBuilder().setKey(key).setExpireTime(expireTime))
                .build();
    }

    public static Proto.Request newRewriteLogRequest() {
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.RewriteLog)
                .setRewriteLogRequest(Proto.RewriteLogRequest.newBuilder())
                .build();
    }

    public static Proto.Request newInnerRewriteLogFinishRequest(boolean ok){
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerRewriteLogFinish)
                .setInnerRewriteLogFinishRequest(Proto.InnerRewriteLogFinishRequest.newBuilder().setOk(ok))
                .build();
    }

    public static Proto.Request newInnerSaveFinishRequest(){
        return Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerSaveFinish)
                .setInnerSaveFinishRequest(Proto.InnerSaveFinishRequest.newBuilder()
                        .setOk(true)
                        .build())
                .build();
    }

    public static void assertStringNotNullOrEmpty(String str) throws InvalidParamException {
        if(Strings.isNullOrEmpty(str))
            throw new InvalidParamException();
    }

    public static void assertCollectionNotEmpty(Collection<String> collection) throws InvalidParamException {
        if(collection.isEmpty())
            throw new InvalidParamException();
    }

    public static void assertIntNotNegative(int aInt) throws InvalidParamException {
        if(aInt < 0)
            throw new InvalidParamException();
    }
}
