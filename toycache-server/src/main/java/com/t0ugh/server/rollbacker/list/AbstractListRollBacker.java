package com.t0ugh.server.rollbacker.list;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.AbstractRollBacker;

public abstract class AbstractListRollBacker extends AbstractRollBacker {

    public AbstractListRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public DBProto.ValueType getValueType() {
        return DBProto.ValueType.ValueTypeList;
    }
}
