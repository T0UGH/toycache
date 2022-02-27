package com.t0ugh.server.rollbacker.sort;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 有的原先存在有的原先不存在
 * 先全Remove掉
 * 原先就存在的 -> 恢复为原先的值
 * 因此doBefore时要判断哪些存在哪些不存在以进行不同的处理
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.ZAdd)
public class ZAddRollBacker extends AbstractSortRollBacker{

    NavigableSet<MemoryComparableString> originExists = Sets.newTreeSet();
    NavigableSet<MemoryComparableString> allAdded = Sets.newTreeSet();

    public ZAddRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        getGlobalContext().getStorage().zRem(getKey(), allAdded.stream().map(MemoryComparableString::getStringValue).collect(Collectors.toSet()));
        getGlobalContext().getStorage().zAdd(getKey(), originExists);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.ZAddRequest zAddRequest = request.getZAddRequest();
        allAdded = zAddRequest.getValuesList().stream()
                .filter(v -> !Strings.isNullOrEmpty(v.getStringValue()))
                .map(MemoryComparableString::parseFrom).collect(Collectors.toCollection(Sets::newTreeSet));
        for (MemoryComparableString m: allAdded) {
            Optional<MemoryComparableString> op = getGlobalContext().getStorage().zGet(getKey(), m.getStringValue());
            op.ifPresent(memoryComparableString -> originExists.add(memoryComparableString));
        }
    }
}
