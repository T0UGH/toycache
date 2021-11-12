package com.t0ugh.server.handler;

import com.t0ugh.sdk.proto.Proto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerAnnotation {
    Proto.MessageType type();
    /**
     * 是否需要检测超时
     * */
    boolean checkExpire() default true;
    /**
     * 是否是写请求
     * */
    boolean isWrite() default false;
}
