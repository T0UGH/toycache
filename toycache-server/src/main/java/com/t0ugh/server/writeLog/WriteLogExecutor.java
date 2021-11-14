package com.t0ugh.server.writeLog;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;
import com.t0ugh.server.utils.MessageUtils;
import com.t0ugh.server.utils.WriteLogUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

@Slf4j
public class WriteLogExecutor extends AbstractMessageExecutor {

    public WriteLogExecutor(GlobalContext globalContext){
        super(globalContext, Executors.newSingleThreadExecutor());
    }

    @Override
    public Proto.Response doRequest(Proto.Request request) throws IOException, InterruptedException {
        if(Objects.equals(Proto.MessageType.InnerRewriteLog, request.getMessageType())){
            doRewriteLogRequest(request);
            return null;
        }
        request.writeDelimitedTo(getGlobalContext().getWriteLogOutputStream());
        // todo: 这个return null是不是不太好
        return null;
    }

    private void doRewriteLogRequest(Proto.Request request) throws IOException, InterruptedException {
        // 检查体积是否超过上限制
//        if(!sizeExceedsThreshold()){
//            getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerRewriteLogFinishRequest(false));
//            return;
//        }
        Proto.InnerRewriteLogRequest rewriteLogRequest = request.getInnerRewriteLogRequest();
        // 清空OutputStream
        OutputStream newOut = WriteLogUtils.clearOutputStream(getGlobalContext().getWriteLogOutputStream(),
                getGlobalContext().getConfig().getWriteLogBaseFilePath());
        getGlobalContext().setWriteLogOutputStream(newOut);
        DBProto.Database db = rewriteLogRequest.getDb();
        // 将数据库转换为一堆request
        List<Proto.Request> requests = WriteLogUtils.databaseToRequests(db);
        // 写入这些Request
        for (Proto.Request r : requests) {
            r.writeDelimitedTo(getGlobalContext().getWriteLogOutputStream());
        }
        // 重写完了告诉MemoryOperationExecutor一声
        getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerRewriteLogFinishRequest(true));
    }
}
