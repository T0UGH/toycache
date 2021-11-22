package com.t0ugh.server.rollbacker.string;

import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.storage.MemoryValueObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@RollBackerAnnotation(messageType = Proto.MessageType.Set)
public class SetRollBacker extends AbstractRollBacker {

    String oldValue;

    public SetRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws ValueTypeNotMatchException {
        Proto.DelRequest req = request.getDelRequest();
        oldValue = getGlobalContext().getStorage().get(req.getKey()).get();

    }

    @Override
    public void doRollBack() throws ValueTypeNotMatchException {
        getGlobalContext().getStorage().set(getKey(), oldValue);
    }
}
