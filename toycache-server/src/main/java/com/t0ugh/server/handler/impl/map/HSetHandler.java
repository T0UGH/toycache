package com.t0ugh.server.handler.impl.map;


import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Map;

@HandlerAnnotation(messageType = Proto.MessageType.HSet, handlerType= HandlerType.Write)
public class HSetHandler extends AbstractGenericsHandler<Proto.HSetRequest, Proto.HSetResponse> {

    public HSetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.HSetResponse doHandle(Proto.HSetRequest req) throws Exception {
        Map<String, String> kvs = req.getKvsMap();
        int count = 0;
        for(Map.Entry<String, String> entry: kvs.entrySet()){
            MessageUtils.assertStringNotNullOrEmpty(entry.getKey());
            MessageUtils.assertStringNotNullOrEmpty(entry.getValue());
            getGlobalContext().getStorage().hSet(req.getKey(), entry.getKey(), entry.getValue());
            count ++;
        }
        return Proto.HSetResponse.newBuilder().setCount(count).build();
    }
}
