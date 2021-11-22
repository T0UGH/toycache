package com.t0ugh.server.handler.impl.check.map;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;

import java.util.Map;
import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.CheckHGetAll, handlerType= HandlerType.Check)
public class CheckHGetAllHandler extends AbstractCheckHandler<Proto.CheckHGetAllRequest, Proto.CheckHGetAllResponse> {

    public CheckHGetAllHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckHGetAllResponse doCheckHandle(Proto.CheckHGetAllRequest req) throws Exception {
        Proto.CheckHGetAllResponse.Builder builder = Proto.CheckHGetAllResponse.newBuilder();
        Map<String, String> actualKvs = getGlobalContext().getStorage().hGetAll(req.getKey());
        builder.setPass(Objects.equals(req.getKvsMap(), actualKvs));
        return builder.putAllActualKvs(actualKvs).build();
    }
}
