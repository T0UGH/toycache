package com.t0ugh.server.handler.impl.check.map;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractCheckHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.Objects;
import java.util.Optional;

@HandlerAnnotation(messageType = Proto.MessageType.CheckHGet, handlerType= HandlerType.Check)
public class CheckHGetHandler extends AbstractCheckHandler<Proto.CheckHGetRequest, Proto.CheckHGetResponse> {

    public CheckHGetHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.CheckHGetResponse doCheckHandle(Proto.CheckHGetRequest req) throws Exception {
        MessageUtils.assertStringNotNullOrEmpty(req.getField());
        Proto.CheckHGetResponse.Builder builder = Proto.CheckHGetResponse.newBuilder();
        Optional<String> optionalS = getGlobalContext().getStorage().hGet(req.getKey(), req.getField());
        if(!optionalS.isPresent()){
            if(!Strings.isNullOrEmpty(req.getValue())){
                return builder.setPass(false).build();
            }
            return builder.setPass(true).build();
        }
        builder.setPass(Objects.equals(optionalS.get(), req.getValue()));
        return builder.setActualValue(optionalS.get()).build();
    }

}
