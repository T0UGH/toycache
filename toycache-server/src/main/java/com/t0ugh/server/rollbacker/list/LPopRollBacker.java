package com.t0ugh.server.rollbacker.list;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;

@RollBackerAnnotation(messageType = Proto.MessageType.LPop)
public class LPopRollBacker extends AbstractListRollBacker {

    boolean doNothing;
    String valWillDel;

    public LPopRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        if (!doNothing){
            getGlobalContext().getStorage().lPush(getKey(), Lists.newArrayList(valWillDel));
        }
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {

        Proto.LPopRequest lPopRequest = request.getLPopRequest();

        if(getGlobalContext().getStorage().lLen(lPopRequest.getKey()) < 1) {
            doNothing = true;
            return;
        }
        valWillDel = getGlobalContext().getStorage().lRange(lPopRequest.getKey(), 0, 0).get(0);
    }

}
