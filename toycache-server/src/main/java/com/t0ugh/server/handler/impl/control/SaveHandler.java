package com.t0ugh.server.handler.impl.control;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.utils.DBUtils;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.Save, checkExpire = false, handlerType= HandlerType.Other)
public class SaveHandler extends AbstractGenericsHandler<Proto.SaveRequest, Proto.SaveResponse> {

    public SaveHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public Proto.SaveResponse doHandle(Proto.SaveRequest unused) throws Exception {
        if (Objects.equals(SaveState.Running, getGlobalContext().getGlobalState().getSaveState())){
            return Proto.SaveResponse.newBuilder().setOk(false).build();
        }
        getGlobalContext().getGlobalState().setSaveState(SaveState.Running);
        Proto.Request dbRequest = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerSave)
                .setInnerSaveRequest(Proto.InnerSaveRequest.newBuilder()
                        // todo: 这里实现的比较简单，只是将所有的Cache备份了一遍然后一起存，应改为Redis的方式
                        .setDb(getGlobalContext().getStorage().toUnModifiableDB(getGlobalContext().getGlobalState().getWriteCount().get(),
                                getGlobalContext().getGlobalState().getEpoch()))
                        .setFilePath(DBUtils.genFilePath(getGlobalContext().getConfig().getDbBaseFilePath()))
                        .build())
                .build();
        getGlobalContext().getDbExecutor().submit(dbRequest);
        return Proto.SaveResponse.newBuilder().setOk(true).build();
    }

}
