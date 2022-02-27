package com.t0ugh.server.rollbacker;

import com.t0ugh.sdk.proto.Proto;

public interface RollBacker {
    void beforeHandle(Proto.Request request);
    void rollBack();
}
