package com.t0ugh.server.utils;

import com.t0ugh.sdk.proto.Proto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {
    public static void assertOK(Proto.MessageType messageType, Proto.Response response) throws Exception  {
        assertEquals(messageType, response.getMessageType());
        assertEquals(Proto.ResponseCode.OK, response.getResponseCode());
        Method hasXXXResponseMethod = response.getClass().getMethod("has"+ messageType.getValueDescriptor().getName() +"Response");
        assertTrue((boolean)hasXXXResponseMethod.invoke(response));
    }
}
