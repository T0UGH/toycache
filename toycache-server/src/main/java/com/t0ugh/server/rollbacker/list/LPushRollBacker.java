package com.t0ugh.server.rollbacker.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

/**
 * 创建RollBacker时，通过count来记录本次添加了几个元素
 * 回滚的时候只需要从链表头删除掉这几个元素即可
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.LPush)
public class LPushRollBacker extends AbstractListRollBacker {

    private int count;

    public LPushRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.LPushRequest req = request.getLPushRequest();
        MessageUtils.assertCollectionNotEmpty(req.getValueList());
        MessageUtils.assertAllStringNotNullOrEmpty(req.getValueList());
        count = req.getValueCount();
    }

    @Override
    public void doRollBack() throws Exception {
        getGlobalContext().getStorage().lTrim(getKey(), count, -1);
    }
}
