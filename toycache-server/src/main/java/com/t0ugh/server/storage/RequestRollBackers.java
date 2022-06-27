package com.t0ugh.server.storage;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBacker;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class RequestRollBackers {
    private Deque<RollBacker> rollBackerDeque;
    Map<RollBacker, Proto.Request> rollBackerRequestMap;
    private int maxSize;
    private GlobalContext globalContext;

    public RequestRollBackers(int maxSize, GlobalContext globalContext){
        this.maxSize =  maxSize;
        this.globalContext = globalContext;
        rollBackerDeque = new LinkedList<>();
    }

    /**
     * 需要在Request被实际执行之前调用
     * */
    public void add(Proto.Request request){
        RollBacker rollBacker = globalContext.getRollBackerFactory().getRollBacker(request.getMessageType())
                .orElseThrow(UnsupportedOperationException::new);
        rollBacker.beforeHandle(request);
        rollBackerDeque.addLast(rollBacker);
        rollBackerRequestMap.put(rollBacker, request);
        if (rollBackerDeque.size() > maxSize){
            RollBacker removed = rollBackerDeque.pollFirst();
            rollBackerRequestMap.remove(removed);
        }
    }

    public boolean rollBack(int n){
        if (rollBackerDeque.size() < n){
            return false;
        }
        for (int i = 0; i < n; i++) {
            RollBacker removed = rollBackerDeque.pollLast();
            rollBackerRequestMap.remove(removed);
            removed.rollBack();
        }
        return true;
    }
}
