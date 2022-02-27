package com.t0ugh.server.rollbacker.list;

import com.google.common.collect.Lists;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RollBackerAnnotation(messageType = Proto.MessageType.LTrim)
public class LTrimRollBacker extends AbstractListRollBacker {

    private List<String> headList = Lists.newArrayList();
    private List<String> tailList = Lists.newArrayList();

    public LTrimRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        getGlobalContext().getStorage().lPush(getKey(), headList);
        getGlobalContext().getStorage().rPush(getKey(), tailList);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.LTrimRequest req = request.getLTrimRequest();
        headList = getGlobalContext().getStorage().lRange(req.getKey(),0,req.getStart());
        headList.remove(headList.size() - 1);
        tailList = getGlobalContext().getStorage().lRange(req.getKey(),req.getEnd(),-1);
        tailList.remove(0);
    }
}
