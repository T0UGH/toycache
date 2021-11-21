package com.t0ugh.sdk.exception;

import com.t0ugh.sdk.proto.Proto;
import lombok.Getter;

public class CheckNotPassException extends Exception{
    @Getter
    private final Object response;
    @Getter
    private final Proto.MessageType messageType;
    public CheckNotPassException(Proto.MessageType messageType, Object response){
        this.messageType = messageType;
        this.response = response;
    }
}
