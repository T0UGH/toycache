package com.t0ugh.server.rollbacker.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;

/**
 * 把之前添加的删掉就行
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.SAdd)
public class SAddRollBacker extends AbstractSetRollBacker {

    public SAddRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        Proto.SAddRequest sAddRequest = getRequest().getSAddRequest();
        getGlobalContext().getStorage().sRem(sAddRequest.getKey(), Sets.newHashSet(sAddRequest.getSetValueList()));
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.SAddRequest sAddRequest = request.getSAddRequest();
        MessageUtils.assertCollectionNotEmpty(sAddRequest.getSetValueList());
        MessageUtils.assertAllStringNotNullOrEmpty(sAddRequest.getSetValueList());
    }
}
