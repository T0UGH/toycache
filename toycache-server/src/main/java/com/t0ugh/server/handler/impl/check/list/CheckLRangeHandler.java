package com.t0ugh.server.handler.impl.check.list;

import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.handler.impl.AbstractHandler;

import java.util.List;
import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.CheckLRange, handlerType= HandlerType.Check)
public class CheckLRangeHandler extends AbstractCheckHandler<Proto.CheckLRangeRequest, Proto.CheckLRangeResponse> {

    public CheckLRangeHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckLRangeResponse doCheckHandle(Proto.CheckLRangeRequest req) throws Exception {
        List<String> values = getGlobalContext().getStorage().lRange(req.getKey(), req.getStart(), req.getEnd());
        List<String> expectedValues = req.getValuesList();
        if(!Objects.equals(values, expectedValues)){
            return Proto.CheckLRangeResponse.newBuilder().setPass(false).addAllValues(expectedValues).build();
        }
        return Proto.CheckLRangeResponse.newBuilder().setPass(true).addAllValues(expectedValues).build();
    }
}
