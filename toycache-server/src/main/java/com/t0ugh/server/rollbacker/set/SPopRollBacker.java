package com.t0ugh.server.rollbacker.set;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;

import java.util.Set;


/**
 * 因为SPop操作会随机弹出元素, 因此该方法需要存储所有的旧状态，然后复原
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.SPop)
public class SPopRollBacker extends AbstractSetRollBacker{

    Set<String> members;

    public SPopRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        getGlobalContext().getStorage().sAdd(getKey(), members);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        members = getGlobalContext().getStorage().sMembers(getKey());
    }

}

