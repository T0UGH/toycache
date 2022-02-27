package com.t0ugh.server.rollbacker.key;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;

public abstract class AbstractAllValueTypeRollBacker extends AbstractRollBacker {
    public AbstractAllValueTypeRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }
    @Override
    public DBProto.ValueType getValueType() {
        return DBProto.ValueType.ValueTypeAll;
    }
}
