package com.t0ugh.server.handler.impl;

import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.utils.MessageUtils;

import java.lang.reflect.Field;

public abstract class AbstractCheckHandler<Req, Res> extends AbstractGenericsHandler<Req, Res>{

    public AbstractCheckHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Res doHandle(Req req) throws Exception{
        Res res = doCheckHandle(req);
        Field passField = res.getClass().getDeclaredField("pass_");
        passField.setAccessible(true);
        boolean pass = passField.getBoolean(res);
        if(!pass){
            throw new CheckNotPassException(getMessageType(), res);
        }
        return res;
    }

    protected abstract Res doCheckHandle(Req req) throws Exception;
}
