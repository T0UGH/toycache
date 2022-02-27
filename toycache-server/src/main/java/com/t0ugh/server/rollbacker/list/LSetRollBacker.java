package com.t0ugh.server.rollbacker.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.utils.MessageUtils;
import com.t0ugh.server.utils.StorageUtils;

@RollBackerAnnotation(messageType = Proto.MessageType.LSet)
public class LSetRollBacker extends AbstractListRollBacker {

    boolean doNothing;
    String oldVal;
    Proto.LSetRequest lSetRequest;

    public LSetRollBacker(GlobalContext globalContext) {
        super(globalContext);
        doNothing = false;
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        try {
            lSetRequest = request.getLSetRequest();
            MessageUtils.assertStringNotNullOrEmpty(lSetRequest.getNewValue());
            int size = getGlobalContext().getStorage().lLen(lSetRequest.getKey());
            StorageUtils.assertAndConvertIndex(lSetRequest.getIndex(), size);
            oldVal = getGlobalContext().getStorage().
                    lRange(lSetRequest.getKey(), lSetRequest.getIndex(), lSetRequest.getIndex()).get(0);
        } catch (IndexOutOfBoundsException e) {
            doNothing = true;
        }
    }

    @Override
    public void doRollBack() throws Exception {
        if(!doNothing){
            getGlobalContext().getStorage().lSet(lSetRequest.getKey(), lSetRequest.getIndex(), oldVal);
        }
    }

}
