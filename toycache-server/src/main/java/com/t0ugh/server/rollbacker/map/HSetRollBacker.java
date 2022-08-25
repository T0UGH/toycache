package com.t0ugh.server.rollbacker.map;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.storage.MemoryValueObject;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Map;
import java.util.Optional;

/**
 * 原来没有field的 -> 应该删掉field
 * 原来有field的 -> 应该恢复field对应的老value
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.HSet)
public class HSetRollBacker extends AbstractMapRollBacker {

    Map<String, String> kvs;

    public HSetRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        Proto.HSetRequest hSetRequest = getRequest().getHSetRequest();
        getGlobalContext().getStorage().backdoor().put(hSetRequest.getKey(), MemoryValueObject.newInstance(kvs));
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.HSetRequest hSetRequest = request.getHSetRequest();
        this.kvs = getGlobalContext().getStorage().hGetAll(hSetRequest.getKey());
    }
}
