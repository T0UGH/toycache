package com.t0ugh.server.rollbacker.map;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Optional;

/**
 * 原来没有field的 -> 应该删掉field
 * 原来有field的 -> 应该恢复field对应的老value
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.HSet)
public class HSetRollBacker extends AbstractMapRollBacker {

    Optional<String> oldValue;

    public HSetRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        Proto.HSetRequest hSetRequest = getRequest().getHSetRequest();
        // 如果key存在并且原来有field
        if (oldValue.isPresent()){
            getGlobalContext().getStorage().hSet(hSetRequest.getKey(), hSetRequest.getField(), oldValue.get());
        } else {
            getGlobalContext().getStorage().hDel(hSetRequest.getKey(), Sets.newHashSet(hSetRequest.getField()));
        }
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.HSetRequest hSetRequest = request.getHSetRequest();
        MessageUtils.assertStringNotNullOrEmpty(hSetRequest.getField());
        oldValue = getGlobalContext().getStorage().hGet(hSetRequest.getKey(), hSetRequest.getField());
    }
}
