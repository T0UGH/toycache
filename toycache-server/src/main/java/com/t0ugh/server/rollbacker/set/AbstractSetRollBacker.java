package com.t0ugh.server.rollbacker.set;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;

public abstract class AbstractSetRollBacker extends AbstractRollBacker {

    public AbstractSetRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public DBProto.ValueType getValueType() {
        return DBProto.ValueType.ValueTypeSet;
    }
}
