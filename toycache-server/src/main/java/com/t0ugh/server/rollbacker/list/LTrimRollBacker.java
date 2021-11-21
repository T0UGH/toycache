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

@RequiredArgsConstructor
@RollBackerAnnotation(messageType = Proto.MessageType.LTrim)
public class LTrimRollBacker implements RollBacker {

    private boolean doNothing;
    private List<String> headList = Lists.newArrayList();
    private List<String> tailList = Lists.newArrayList();
    private String key;

    @NonNull private GlobalContext globalContext;

    @Override
    public void init(Proto.Request request) {
        try {
            Proto.LTrimRequest req = request.getLTrimRequest();
            doNothing = !globalContext.getStorage().exists(req.getKey());
            if(doNothing){
                return;
            }
            key = req.getKey();
            headList = globalContext.getStorage().lRange(req.getKey(),0,req.getStart());
            tailList = globalContext.getStorage().lRange(req.getKey(),req.getEnd(),-1);
        } catch (ValueTypeNotMatchException e) {
            doNothing = true;
        }

    }

    @Override
    public void rollBack() {
        try{
            if(doNothing){
                return;
            }
            globalContext.getStorage().lPush(key, headList);
            globalContext.getStorage().rPush(key, tailList);
        } catch (ValueTypeNotMatchException ignored) {}

    }
}
