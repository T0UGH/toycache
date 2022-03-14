package com.t0ugh.server.callback;

import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SyncSlaveResponseCallback implements Callback {

    private GlobalContext globalContext;

    @Override
    public void callback(Proto.Request request, Proto.Response response) {
        globalContext.getMemoryOperationExecutor().submit(Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.InnerUpdateLastWriteId)
                .setInnerUpdateLastWriteIdRequest(Proto.InnerUpdateLastWriteIdRequest.newBuilder()
                        .setClusterId(response.getSyncResponse().getClusterId())
                        .setServerId(response.getSyncResponse().getServerId())
                        .setLastWriteId(response.getSyncResponse().getLastWriteId())
                        .build())
                .build());
    }
}
