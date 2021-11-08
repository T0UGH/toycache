package com.t0ugh.server.utils;

import com.google.common.base.Function;
import com.t0ugh.sdk.proto.Proto;

import java.util.HashMap;
import java.util.Map;

public class MessageUtils {

    public static Proto.Response.Builder okBuilder() {
        return Proto.Response.newBuilder()
                .setResponseCode(Proto.ResponseCode.OK);
    }

    public static Proto.Response responseWithCode(Proto.ResponseCode code) {
        return Proto.Response.newBuilder()
                .setResponseCode(code).build();
    }

    private static final Map<Proto.MessageType, Function<Proto.Request, Boolean>> checkerMap = new HashMap<>();
    static {
        // todo 有没有更简单的方式, 没有的话每次都需要往这里面添加
        checkerMap.put(Proto.MessageType.Exists, Proto.Request::hasExistsRequest);
        checkerMap.put(Proto.MessageType.Del, Proto.Request::hasDelRequest);
        checkerMap.put(Proto.MessageType.Get, Proto.Request::hasGetRequest);
        checkerMap.put(Proto.MessageType.Set, Proto.Request::hasSetRequest);
    }

    /**
     * 检查请求中是否包含对应MessageType的子请求
     * */
    public static boolean containRequest(Proto.Request request) {
        return checkerMap.get(request.getMessageType()).apply(request);
    }
}
