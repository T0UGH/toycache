package com.t0ugh.server.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.storage.MemoryValueObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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

    public static<E> void assertCollectionEquals(Collection<E> actual, Collection<E> expect) throws Exception {
        assertEquals(actual.size(), expect.size());
        for (E e: actual){
            assertTrue(expect.contains(e));
        }
    }


    public static Object copyCollectionByValueType(MemoryValueObject memoryValueObject){
        switch (memoryValueObject.getValueType()){
            case ValueTypeSet:
                return Sets.newHashSet(memoryValueObject.getSetValue());
            case ValueTypeList:
                return Lists.newArrayList(memoryValueObject.getListValue());
            case ValueTypeSortedSet:
                return Sets.newTreeSet(memoryValueObject.getSortedSetValue().backdoorS());
        }
        return null;
    }

    public static void testRollBackerCollectionUnchanged(Proto.Request request, String key, GlobalContext testContext) throws Exception {
        Collection<String> origin = (Collection<String>) copyCollectionByValueType(testContext.getStorage().backdoor().get(key));
        RollBacker rollBacker = testContext.getRollBackerFactory().getRollBacker(request.getMessageType()).get();
        rollBacker.beforeHandle(request);
        Handler handler = testContext.getHandlerFactory().getHandler(request.getMessageType()).get();
        Proto.Response response = handler.handle(request);
        TestUtils.assertOK(request.getMessageType(), response);
        rollBacker.rollBack();
        TestUtils.assertCollectionEquals(origin, (Collection<String>) copyCollectionByValueType(testContext.getStorage().backdoor().get(key)));
    }
}
