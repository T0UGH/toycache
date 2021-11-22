package com.t0ugh.server.handler.impl.sort;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.NavigableSet;
import java.util.stream.Collectors;

@HandlerAnnotation(messageType = Proto.MessageType.ZAdd, handlerType= HandlerType.Read)
public class ZAddHandler extends AbstractGenericsHandler<Proto.ZAddRequest, Proto.ZAddResponse> {

    public ZAddHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.ZAddResponse doHandle(Proto.ZAddRequest zAddRequest) throws Exception {
        NavigableSet<MemoryComparableString> ns = zAddRequest.getValuesList().stream()
                .filter(v -> !Strings.isNullOrEmpty(v.getStringValue()))
                .map(MemoryComparableString::parseFrom).collect(Collectors.toCollection(Sets::newTreeSet));
        int added = getGlobalContext().getStorage().zAdd(zAddRequest.getKey(), ns);
        return Proto.ZAddResponse.newBuilder().setAdded(added).build();
    }
}
