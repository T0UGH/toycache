package com.t0ugh.server.handler.impl.multi;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.SaveState;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.rollbacker.RollBacker;
import com.t0ugh.server.utils.MessageUtils;
import com.t0ugh.server.utils.StateUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

@Slf4j
@HandlerAnnotation(messageType = Proto.MessageType.Multi, handlerType= HandlerType.Other)
public class MultiHandler implements Handler {

    private final GlobalContext globalContext;

    public MultiHandler(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public Proto.Response handle(Proto.Request request) {
        try {
            if (!request.hasMultiRequest()) {
                return MessageUtils.responseWithCode(Proto.ResponseCode.InvalidParam, request.getClientTId());
            }

            Proto.MultiRequest multiRequest = request.getMultiRequest();
            Stack<RollBacker> rollBackerStack = new Stack<>();
            Proto.MultiResponse.Builder multiResponseBuilder = Proto.MultiResponse.newBuilder();
            multiResponseBuilder.setPass(true);
            StateUtils.startTransaction(globalContext);
            for (Proto.Request currReq : multiRequest.getRequestsList()) {
                // 先判断事务支不支持这种MessageType, 如果不支持直接break
                if (!MessageUtils.isTransactionSupported(currReq.getMessageType(), globalContext.getHandlerFactory())) {
                    MessageUtils.messageTypeNotSupportedMultiResponseBuilder(multiResponseBuilder, currReq);
                    break;
                }
                // 如果是写命令需要创建一个RollBacker并且压入栈中
                if (MessageUtils.isWriteRequest(currReq.getMessageType(), globalContext.getHandlerFactory())) {
                    RollBacker rollBacker = globalContext.getRollBackerFactory().getRollBacker(currReq.getMessageType())
                            .orElseThrow(UnsupportedOperationException::new);
                    rollBacker.beforeHandle(currReq);
                    rollBackerStack.push(rollBacker);
                }
                Proto.Response currResp = globalContext.getHandlerFactory().getHandler(currReq.getMessageType())
                        .orElseThrow(UnsupportedOperationException::new).handle(currReq);
                // 然后检查currResp是否OK, 如果不OK就break
                if (!Objects.equals(Proto.ResponseCode.OK, currResp.getResponseCode())) {
                    MessageUtils.failMultiResponseBuilder(multiResponseBuilder, currReq, currResp);
                    break;
                }
                // ok了就把Response添加一下
                multiResponseBuilder.addResponses(currResp);
            }

            if (multiResponseBuilder.getPass()) {
                // 如果事务通过就需要将所有写请求写入写日志
                for (Proto.Request currReq : multiRequest.getRequestsList()) {
                    if (MessageUtils.isWriteRequest(request.getMessageType(), globalContext.getHandlerFactory())) {
                        // 更新一下内部状态
                        Field field = request.getClass().getDeclaredField("writeId_");
                        field.setAccessible(true);
                        field.set(request, globalContext.getGlobalState().getWriteCount().incrementAndGet());
                        // 向writeLogExecutor提交一个写日志
                        globalContext.getWriteLogExecutor().submit(request);
                        // 如果开启了RDB, 放到RDB里
                        if (Objects.equals(globalContext.getGlobalState().getSaveState(), SaveState.Running)
                                && Objects.equals(request.getMessageType(), Proto.MessageType.Expire)) {
                            globalContext.getRdbBuffer().getExpireList().add(request);
                        } else if (Objects.equals(globalContext.getGlobalState().getSaveState(), SaveState.Running)){
                            String key = MessageUtils.getKeyFromRequest(request).get();
                            List<Proto.Request> requests = globalContext.getRdbBuffer().getBufferMap().getOrDefault(key, new ArrayList<>());
                            requests.add(request);
                            globalContext.getRdbBuffer().getBufferMap().put(key, requests);
                        }
                    }
                }
            } else {
                // 如果事务没有通过就需要回滚
                while (!rollBackerStack.isEmpty()) {
                    RollBacker curr = rollBackerStack.pop();
                    curr.rollBack();
                }
            }

            StateUtils.endTransaction(globalContext);
            return MessageUtils.okBuilder(Proto.MessageType.Multi)
                    .setMultiResponse(multiResponseBuilder)
                    .build();
        } catch (Exception e) {
            log.error("MyUnknown Exception: ", e);
            return MessageUtils.responseWithCode(Proto.ResponseCode.Unknown, request.getClientTId());
        }
    }
}
