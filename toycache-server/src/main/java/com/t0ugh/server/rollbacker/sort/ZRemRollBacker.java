package com.t0ugh.server.rollbacker.sort;

import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.NavigableSet;
import java.util.Optional;

/**
 * 原来就存在的就恢复回去
 * 原先不存在的不要恢复回去
 * 因此doBefore时要判断哪些存在哪些不存在以进行不同的处理
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.ZRem)
public class ZRemRollBacker extends AbstractSortRollBacker{

    NavigableSet<MemoryComparableString> originExists = Sets.newTreeSet();

    public ZRemRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        getGlobalContext().getStorage().zAdd(getKey(), originExists);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.ZRemRequest zRemRequest = request.getZRemRequest();
        for (String s: zRemRequest.getMembersList()) {
            Optional<MemoryComparableString> op = getGlobalContext().getStorage().zGet(getKey(), s);
            op.ifPresent(memoryComparableString -> originExists.add(memoryComparableString));
        }

    }
}
