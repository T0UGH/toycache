package com.t0ugh.server.handler.impl.sortedset;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.storage.MemoryComparableString;

import java.util.NavigableSet;
import java.util.stream.Collectors;

@HandlerAnnotation(type = Proto.MessageType.ZAdd, isWrite = true)
public class ZAddHandler extends AbstractHandler {

    public ZAddHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception {
        Proto.ZAddRequest req = request.getZAddRequest();
        NavigableSet<MemoryComparableString> ns = req.getValuesList().stream()
                .filter(v -> !Strings.isNullOrEmpty(v.getStringValue()))
                .map(MemoryComparableString::parseFrom).collect(Collectors.toCollection(Sets::newTreeSet));
        int added = getGlobalContext().getStorage().zAdd(req.getKey(), ns);
        responseBuilder.setZAddResponse(Proto.ZAddResponse.newBuilder().setAdded(added));
    }
}
