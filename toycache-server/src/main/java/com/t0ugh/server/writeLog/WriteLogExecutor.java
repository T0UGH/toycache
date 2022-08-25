package com.t0ugh.server.writeLog;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.executor.AbstractMessageExecutor;
import com.t0ugh.server.utils.MessageUtils;
import com.t0ugh.server.utils.WriteLogUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

@Slf4j
public class WriteLogExecutor extends AbstractMessageExecutor {

    private Map<String, Long> expireMap;
    private List<String> keyList;
    private int nextKeyIndex;
    private OutputStream tmpOutputStream;


    public WriteLogExecutor(GlobalContext globalContext){
        super(globalContext, Executors.newSingleThreadExecutor());
        refresh();
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

    private void handleInnerRewriteLogSendKeyListRequest(Proto.Request request) throws IOException {
        Proto.InnerRewriteLogSendKeyListRequest innerRewriteLogSendKeyListRequest = request.getInnerRewriteLogSendKeyListRequest();
        refresh();
        // 存一下expireMap和keyList
        this.expireMap = innerRewriteLogSendKeyListRequest.getExpireMap();
        this.keyList = innerRewriteLogSendKeyListRequest.getKeysList();
        // 开启一个新的OutputStream
        String baseFilePath = getGlobalContext().getConfig().getWriteLogBaseFilePath();
        tmpOutputStream = new FileOutputStream(WriteLogUtils.getTmpWriteLogFilePath(baseFilePath));
        // 发消息给ME,请求下一个key
        // 如果已经没有下一个key直接发完成就行
        finishKeysIfSatisfied();
        String nextKey = keyList.get(nextKeyIndex);
        getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerRewriteLogNextKeyRequest(nextKey));
    }

    private void finishKeysIfSatisfied() {
        if(nextKeyIndex >= keyList.size()){
            getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newNoopRequest(getGlobalContext().getConfig().getNoopKeyForRewriteLog()));
            getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerRewriteLogFinishKeysRequest());
        }
    }

    private void refresh(){
        expireMap = null;
        keyList = null;
        nextKeyIndex = 0;
        tmpOutputStream = null;
    }

    private void handleInnerRewriteLogSendOneKey(Proto.Request request) throws IOException {
        Proto.InnerRewriteLogSendOneKeyRequest innerRewriteLogSendOneKeyRequest = request.getInnerRewriteLogSendOneKeyRequest();
        if(innerRewriteLogSendOneKeyRequest.getExist()){
            Proto.Request requestToWrite = WriteLogUtils.valueObjectToRequest(
                    innerRewriteLogSendOneKeyRequest.getKey(),
                    innerRewriteLogSendOneKeyRequest.getValue());
            requestToWrite.writeDelimitedTo(tmpOutputStream);
        }
        finishKeysIfSatisfied();
        String nextKey = keyList.get(nextKeyIndex);
        getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerRewriteLogNextKeyRequest(nextKey));
    }

    private void handleInnerRewriteLogSendLogs(Proto.Request request) throws IOException {
        Proto.InnerRewriteLogSendLogsRequest innerRewriteLogSendLogsRequest = request.getInnerRewriteLogSendLogsRequest();
        // 先写文件
        for(Proto.Request requestToWrite: innerRewriteLogSendLogsRequest.getRequestsList()){
            requestToWrite.writeDelimitedTo(tmpOutputStream);
        }
        // 然后替换文件描述符和outputStream
        replaceRewriteLogFileAndOutputStream();
        // 重写完了告诉MemoryOperationExecutor一声
        getGlobalContext().getMemoryOperationExecutor().submit(MessageUtils.newInnerRewriteLogFinishRequest(true));

    }

    private void replaceRewriteLogFileAndOutputStream() throws IOException {
        this.tmpOutputStream.close();
        getGlobalContext().getWriteLogOutputStream().close();
        String baseFilePath = getGlobalContext().getConfig().getWriteLogBaseFilePath();
        File oldFile = new File(WriteLogUtils.getWriteLogFilePath(baseFilePath));
        oldFile.deleteOnExit();
        File newFile = new File(WriteLogUtils.getTmpWriteLogFilePath(baseFilePath));
        newFile.renameTo(oldFile);
        OutputStream writeLogOutputStream = new FileOutputStream(WriteLogUtils
                .getWriteLogFilePath(getGlobalContext().getConfig().getWriteLogBaseFilePath()), true);
        getGlobalContext().setWriteLogOutputStream(writeLogOutputStream);
    }


    private void doRewriteLogRequest(Proto.Request request) throws IOException, InterruptedException {
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
