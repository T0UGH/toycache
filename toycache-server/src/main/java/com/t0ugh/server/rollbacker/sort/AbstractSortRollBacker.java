package com.t0ugh.server.rollbacker.sort;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;

public abstract class AbstractSortRollBacker extends AbstractRollBacker {

    public AbstractSortRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public DBProto.ValueType getValueType() {
        return DBProto.ValueType.ValueTypeSortedSet;
    }
}
