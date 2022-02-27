package com.t0ugh.server.rollbacker.map;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.impl.AbstractHandler;
import com.t0ugh.server.rollbacker.AbstractRollBacker;

public abstract class AbstractMapRollBacker extends AbstractRollBacker {
    public AbstractMapRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public DBProto.ValueType getValueType() {
        return DBProto.ValueType.ValueTypeMap;
    }
}
