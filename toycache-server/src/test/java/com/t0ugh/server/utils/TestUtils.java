package com.t0ugh.server.utils;

import com.t0ugh.sdk.proto.Proto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {
    public static void assertOK(Proto.MessageType messageType, Proto.Response response) throws Exception  {
        assertEquals(messageType, response.getMessageType());
        assertEquals(Proto.ResponseCode.OK, response.getResponseCode());
        Method hasXXXResponseMethod = response.getClass().getMethod("has"+ messageType.getValueDescriptor().getName() +"Response");
        assertTrue((boolean)hasXXXResponseMethod.invoke(response));
    }

    public static<K, V> void assertMapEquals(Map<K, V> actual, Map<K, V> expect) throws Exception {
        assertEquals(actual.size(), expect.size());
        for (Map.Entry<K, V> entry: actual.entrySet()) {
            assertTrue(expect.containsKey(entry.getKey()));
            assertEquals(entry.getValue(), expect.get(entry.getKey()));
        }
    }
}
