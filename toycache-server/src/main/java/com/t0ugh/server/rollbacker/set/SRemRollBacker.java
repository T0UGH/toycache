package com.t0ugh.server.rollbacker.set;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;

import java.util.Objects;
import java.util.Set;

/**
 * 把原先就存在的给加回来，原先就不存在的也不要了
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.SRem)
public class SRemRollBacker extends AbstractSetRollBacker {

    Set<String> originExists = Sets.newHashSet();

    public SRemRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        Proto.SRemRequest sRemRequest = getRequest().getSRemRequest();
        getGlobalContext().getStorage().sAdd(sRemRequest.getKey(), originExists);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.SRemRequest sRemRequest = request.getSRemRequest();
        for(String s: sRemRequest.getMembersList()){
            if(getGlobalContext().getStorage().sIsMember(getKey(), s)){
                originExists.add(s);
            }
        }
    }
}
