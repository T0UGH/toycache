package com.t0ugh.server.handler.impl.replicate;

import com.t0ugh.sdk.proto.DBProto;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.utils.MessageUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SyncHandler implements Handler {

    private final GlobalContext globalContext;

    public SyncHandler(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public Proto.Response handle(Proto.Request request) {
        // 1 先判断合不合适
        if (!request.hasSyncRequest()) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.InvalidParam, request.getClientTId());
        }
        Proto.SyncRequest syncRequest = request.getSyncRequest();
        // 判断是不是同一个组
        if (Objects.equals(syncRequest.getGroupId(), globalContext.getGlobalState().getGroupId())){
            return MessageUtils.responseWithCode(Proto.ResponseCode.SyncError, request.getClientTId());
        }
        // 判断是不是老领导, 老领导的命令就不听了
        if (syncRequest.getEpoch() < globalContext.getGlobalState().getEpoch()){
            return MessageUtils.responseWithCode(Proto.ResponseCode.SyncError, request.getClientTId());
        }
        // 判断是不是没东西, 没东西就不更新但是返回一个OK
        if (!syncRequest.hasDb() && syncRequest.getSyncRequestsCount() == 0){
            return MessageUtils.okBuilder(Proto.MessageType.Sync).setSyncResponse(Proto.SyncResponse.newBuilder().build()).build();
        }
        // 如果有快照就应用快照
        if (syncRequest.hasDb()){
            DBProto.Database db = syncRequest.getDb();
            globalContext.getStorage().applyDb(syncRequest.getDb());
            globalContext.getGlobalState().getWriteCount().set(db.getLastWriteId());
            return MessageUtils.okBuilder(Proto.MessageType.Sync).setSyncResponse(Proto.SyncResponse.newBuilder().build()).build();
        }
        // 如果没有快照就看看能不能应用Request
        // 1 有空隙和严丝合缝都不放
        // 2 没有空隙的时候要检查第一条是否符合, 符合的话就用后面的替换掉
        // 3 不符合就不应用, 并且把自己的updateCount退一格
        List<Proto.Request> requests = syncRequest.getSyncRequestsList();
        Proto.Request firstRequest = requests.get(0);
        Optional<Proto.Request> localRequest = globalContext.getRequestBuffer().get(firstRequest.getWriteId());
        if (!localRequest.isPresent()){
            // 不合适, 就返回一个错误就行
            return MessageUtils.responseWithCode(Proto.ResponseCode.SyncError, request.getClientTId());
        }
        // 没匹配上, 别忘了退格
        if(localRequest.get().getEpoch() != firstRequest.getEpoch()){
            globalContext.getGlobalState().getWriteCount().set(firstRequest.getWriteId());
            return MessageUtils.responseWithCode(Proto.ResponseCode.SyncError, request.getClientTId());
        }
        // 匹配上了, 先将系统回滚到指定位置
        int rollBackCount = (int) (globalContext.getGlobalState().getWriteCount().get() - firstRequest.getWriteId());
        boolean success = globalContext.getRequestRollBackers().rollBack(rollBackCount);
        // 如果不成功, 说明系统回滚不过去了, 直接将WriteCount置为0, 然后发快照吧
        if (!success){
            globalContext.getGlobalState().getWriteCount().set(0L);
        }
        for(Proto.Request curr: requests){
            // todo: 这里直接调用的是那个老的，需要改一下AbstractHandler里的逻辑
            globalContext.getHandlerFactory().getHandler(curr.getMessageType()).orElseThrow(UnsupportedOperationException::new).handle(request);
        }
        return MessageUtils.okBuilder(Proto.MessageType.Sync).setSyncResponse(Proto.SyncResponse.newBuilder().build()).build();
    }

}
