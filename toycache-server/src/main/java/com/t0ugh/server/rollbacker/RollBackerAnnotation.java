package com.t0ugh.server.rollbacker;

import com.t0ugh.sdk.proto.Proto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RollBackerAnnotation {
    Proto.MessageType messageType();
}
