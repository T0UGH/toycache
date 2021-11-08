package com.t0ugh.server.handler;

import com.t0ugh.sdk.proto.Proto;

/**
 * 请求处理器
 * */
public interface Handler {

    /**
     * 实际的请求处理
     * @param request 请求
     * @return 响应
     * */
    Proto.Response handle(Proto.Request request);
}
