package com.t0ugh.server.handler.impl.set;


import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Set;


@HandlerAnnotation(messageType = Proto.MessageType.SAdd, handlerType= HandlerType.Write)
public class SAddHandler extends AbstractGenericsHandler<Proto.SAddRequest, Proto.SAddResponse> {

    public SAddHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SAddResponse doHandle(Proto.SAddRequest sAddRequest) throws Exception {
        // 类型转换, 这一步会删掉重复的元素
        MessageUtils.assertCollectionNotEmpty(sAddRequest.getSetValueList());
        MessageUtils.assertAllStringNotNullOrEmpty(sAddRequest.getSetValueList());
        Set<String> value = Sets.newHashSet(sAddRequest.getSetValueList());
        int added = getGlobalContext().getStorage().sAdd(sAddRequest.getKey(), value);
        return Proto.SAddResponse.newBuilder().setAdded(added).build();
    }
}
