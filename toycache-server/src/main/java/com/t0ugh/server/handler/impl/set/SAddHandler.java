package com.t0ugh.server.handler.impl.set;


import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Set;


@HandlerAnnotation(messageType = Proto.MessageType.SAdd, handlerType= HandlerType.Write)
public class SAddHandler extends AbstractHandler<Proto.SAddRequest, Proto.SAddResponse> {

    public SAddHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SAddResponse doHandle(Proto.SAddRequest sAddRequest) throws Exception {
        // 类型转换, 这一步会删掉重复的元素
        MessageUtils.assertCollectionNotEmpty(sAddRequest.getSetValueList());
        Set<String> value = Sets.newHashSet(sAddRequest.getSetValueList());
        int added = getGlobalContext().getStorage().sAdd(sAddRequest.getKey(), value);
        return Proto.SAddResponse.newBuilder().setAdded(added).build();
    }

    public void doUndo(Proto.SAddRequest sAddRequest) throws Exception {
        // 如果原先key都不存在, 那就直接删key
        // 如果原先key存在, 那么很可能出现替换情况，所以需要记录一下哪些是新添加的，把新添加的删了就行了
    }
}
