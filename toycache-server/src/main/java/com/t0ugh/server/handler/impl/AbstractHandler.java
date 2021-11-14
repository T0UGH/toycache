package com.t0ugh.server.handler.impl;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.HandlerUtils;
import com.t0ugh.server.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 抽象的处理器
 * 这是一个TemplateMethod模式
 * 此外, 为了注册handler，这个类的所有子类都必须带有HandlerAnnotation注解 {@link com.t0ugh.server.handler.HandlerAnnotation()}
 * */
@Slf4j
@AllArgsConstructor
public abstract class AbstractHandler implements Handler {


    @Getter
    private GlobalContext globalContext;


    @Override
    public Proto.Response handle(Proto.Request request){

        try {
            // 首先要校验信息
            if (!MessageUtils.containRequest(request)){
                throw new IllegalArgumentException(String.format("Request[%s] doesn't exist", request.getMessageType().getValueDescriptor().getName()));
            }
            // 这个请求是否需要判断超时
            if (HandlerUtils.needCheckExpire(request.getMessageType(), getGlobalContext().getHandlerFactory())){
                // 检查是否超时, 超时就删Storage和ExpireMap里对应的kv对
                Optional<String> o = MessageUtils.getKeyFromRequest(request);
                o.ifPresent(s -> HandlerUtils.clearStorageAndExpireMapIfExpired(s, getGlobalContext()));
            }
            // 然后调用抽象方法来做实际的处理
            Proto.Response.Builder okBuilder = MessageUtils.okBuilder();
            doHandle(request, okBuilder);
            okBuilder.setMessageType(request.getMessageType());
            // 如果能执行到这里, 基本就是没有异常了, 如果是写请求更新一下内部状态并且向writeLogExecutor提交一个写日志
            if(MessageUtils.isWriteRequest(request.getMessageType(), getGlobalContext().getHandlerFactory())){
                // 更新一下内部状态
                getGlobalContext().getGlobalState().getUpdateCount().incrementAndGet();
                // 向writeLogExecutor提交一个写日志
                getGlobalContext().getWriteLogExecutor().submit(request);
            }
            return okBuilder.build();
        // 统一的异常处理
        } catch (ValueTypeNotMatchException e) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.ValueTypeNotMatch);
        } catch (InvalidParamException e) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.InvalidParam);
        } catch (Exception e) {
            log.error("MyUnknown Exception: ", e);
            return MessageUtils.responseWithCode(Proto.ResponseCode.Unknown);
        }

    }

    /**
     * 实际的业务逻辑处理
     * 实现这个抽象方法的类不需要进行信息校验
     * @param request 请求
     * @param responseBuilder 响应的建造器, 需要把对应的响应放进去
     *                        e.g. responseBuilder.setGetResponse(Proto.GetResponse.newBuilder().setValue(value));
     * */
    public abstract void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception;

}
