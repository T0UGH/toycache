package com.t0ugh.server.handler.impl.replicate;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.Sync, checkExpire = false, handlerType= HandlerType.Other)
public class SyncHandler extends AbstractGenericsHandler<Proto.SyncRequest, Proto.SyncResponse> {

    public SyncHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.SyncResponse doHandle(Proto.SyncRequest syncRequest) throws Exception {
        long masterClusterId = syncRequest.getClusterId();
        if (!Objects.equals(masterClusterId, getGlobalContext().getGlobalState().getClusterId())){
            handleClusterIdUnMatched(syncRequest);
        }
        handleClusterIdMatched(syncRequest);
        return null;
    }

    private void handleClusterIdUnMatched(Proto.SyncRequest syncRequest) throws Exception {
        // 如果clusterId不匹配, 首先要更新clusterId
        getGlobalContext().getGlobalState().setClusterId(syncRequest.getClusterId());
        // 如果携带了Snapshot就直接应用, 并且把writeCount置为对应的值
        // todo: 这个isNull用的对不对
        if (!Objects.isNull(syncRequest.getDb())&&!syncRequest.getDb().getDataMap().isEmpty()){
            getGlobalContext().getStorage().applyDb(syncRequest.getDb());
            getGlobalContext().getGlobalState().getWriteCount().set(syncRequest.getDb().getLastWriteId());
            return;
        }
        // 如果携带了Requests要查看Requests是否是从1开始的
        if (!Objects.equals(0, syncRequest.getSyncRequestsCount())){
            Proto.Request firstRequest = syncRequest.getSyncRequests(0);
            // 是从1开始的就应用
            if (Objects.equals(1, firstRequest.getWriteId())){
                for (Proto.Request request: syncRequest.getSyncRequestsList()) {
                    getGlobalContext().getHandlerFactory().getHandler(request.getMessageType()).get().handle(request);
                    // 这里不用更新writeCount, 因为handle方法会更新这个值
                }
            }
        }

    }

    private void handleClusterIdMatched(Proto.SyncRequest syncRequest) throws Exception {
        // 如果携带了Snapshot, 首先设置
        if (!Objects.isNull(syncRequest.getDb())&&!syncRequest.getDb().getDataMap().isEmpty()){
            if(syncRequest.getDb().getLastWriteId() >= getGlobalContext().getGlobalState().getWriteCount().get()){
                getGlobalContext().getStorage().applyDb(syncRequest.getDb());
                getGlobalContext().getGlobalState().getWriteCount().set(syncRequest.getDb().getLastWriteId());
            }
            return;
        }
        // 如果携带了Requests要查看Requests是否是从1开始的
        if (!Objects.equals(0, syncRequest.getSyncRequestsCount())){
            Proto.Request firstRequest = syncRequest.getSyncRequests(0);
            // 但是第一条Request与lastWriteId有间隔, 则不应用
            long lastWriteId = getGlobalContext().getGlobalState().getWriteCount().get();
            if (firstRequest.getWriteId() > lastWriteId){
                return;
            }
            // lastWriteId过小, 则不应用
            if (syncRequest.getLastWriteId() < lastWriteId){
                return;
            }
            // 否则应用在getGlobalContext().getGlobalState().getWriteCount().get()与lastWriteId之间的数据
            for (Proto.Request request: syncRequest.getSyncRequestsList()) {
                if (request.getWriteId() > lastWriteId){
                    // 这里不用更新writeCount, 因为handle方法会更新这个值
                    getGlobalContext().getHandlerFactory().getHandler(request.getMessageType()).get().handle(request);
                }
            }
        }
    }
}
