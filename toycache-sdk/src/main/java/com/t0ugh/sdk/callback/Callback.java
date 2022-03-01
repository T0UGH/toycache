package com.t0ugh.sdk.callback;

import com.t0ugh.sdk.proto.Proto;

public interface Callback {
    void callback(Proto.Request request, Proto.Response response);
}
