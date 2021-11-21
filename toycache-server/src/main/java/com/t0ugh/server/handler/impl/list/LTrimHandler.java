package com.t0ugh.server.handler.impl.list;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractHandler;

@HandlerAnnotation(messageType = Proto.MessageType.LTrim, handlerType= HandlerType.Write)
public class LTrimHandler extends AbstractHandler<Proto.LTrimRequest, Proto.LTrimResponse> {

    public LTrimHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.LTrimResponse doHandle(Proto.LTrimRequest req) throws Exception {
        boolean ok = getGlobalContext().getStorage().lTrim(req.getKey(), req.getStart(), req.getEnd());
        return Proto.LTrimResponse.newBuilder().setOk(ok).build();
    }

    public void doUndo(Proto.LTrimRequest req){
        // LTrim会删除两部分List -> HeadList -> TailList，有可能有的为空
        // 撤销的时候把HeadList存到Head里, 把TailList存到Tail里
    }
}
