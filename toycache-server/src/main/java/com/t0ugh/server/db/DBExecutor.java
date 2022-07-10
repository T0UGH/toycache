package com.t0ugh.server.db;

import com.google.common.base.Strings;
import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;
import com.t0ugh.server.utils.DBUtils;
import com.t0ugh.server.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class DBExecutor extends AbstractMessageExecutor {

    public DBExecutor(GlobalContext globalContext) {
        super(globalContext, Executors.newSingleThreadExecutor());
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws IOException, ExecutionException, InterruptedException {
        Proto.InnerStartSaveRequest innerStartSaveRequest = request.getInnerStartSaveRequest();
        FileOutputStream fileOutputStream = new FileOutputStream(innerStartSaveRequest.getFilePath());
        try{
            DBProto.DatabaseMeta.Builder metaBuilder = DBProto.DatabaseMeta.newBuilder();
            metaBuilder.putAllExpire(innerStartSaveRequest.getExpireMap());
            // 将每个data写入硬盘
            for (String key: innerStartSaveRequest.getKeysList()) {
                Future<Proto.Response> future = getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerCloneValueRequest(key));
                Proto.Response response = future.get();
                Proto.InnerCloneValueResponse innerCloneValueResponse = response.getInnerCloneValueResponse();
                if (innerCloneValueResponse.hasKeyValue()){
                    DBProto.KeyValue keyValue = innerCloneValueResponse.getKeyValue();
                    keyValue.writeDelimitedTo(fileOutputStream);
                }
            }
            Future<Proto.Response> future = getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerSaveFinishRequest());
            Proto.Response response = future.get();
            List<Proto.Request> rdbBuffer = response.getInnerSaveFinishResponse().getRequestsList();
            metaBuilder.setLastWriteId(response.getInnerSaveFinishResponse().getLastWriteId());
            metaBuilder.setLastEpoch(response.getInnerSaveFinishResponse().getLastEpoch());
            // 将后续的命令写入硬盘
            for (Proto.Request request1: rdbBuffer){
                request1.writeDelimitedTo(fileOutputStream);
            }
            // 先将元数据写入硬盘
            metaBuilder.build().writeDelimitedTo(fileOutputStream);
        } finally {
            fileOutputStream.close();
            return null;
        }
    }
}
