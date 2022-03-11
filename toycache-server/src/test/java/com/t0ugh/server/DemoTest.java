package com.t0ugh.server;

import com.t0ugh.sdk.proto.Proto;
import org.junit.Test;

import java.lang.reflect.Field;

public class DemoTest extends BaseTest{

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.ZAdd)
                .build();
        Field field = request.getClass().getDeclaredField("messageType_");
        field.setAccessible(true);
        field.set(request, Proto.MessageType.ZRem.getNumber());
        System.out.println();
    }


}
              