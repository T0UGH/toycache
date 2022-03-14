package com.t0ugh.server.handler.impl;

import com.t0ugh.sdk.exception.CheckNotPassException;
import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.utils.HandlerUtils;
import com.t0ugh.server.utils.MessageUtils;
import com.t0ugh.server.utils.StateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
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
                throw new InvalidParamException();
            }
            Optional<String> optional = MessageUtils.getKeyFromRequest(request);
            // 如果请求中含有key并且需要判断超时, 就检查超时
            if (optional.isPresent() && HandlerUtils.needCheckExpire(request.getMessageType(), getGlobalContext().getHandlerFactory())){
                // 检查是否超时, 超时就删Storage和ExpireMap里对应的kv对
                getGlobalContext().getStorage().delIfExpired(optional.get());
            }
            // 然后调用抽象方法来做实际的处理
            Proto.Response.Builder okBuilder = MessageUtils.okBuilder(request.getMessageType());
            // 如果能执行到这里, 基本就是没有异常了
            // 如果是写请求并且当前不是在执行事务，就更新一下内部状态并且向writeLogExecutor提交一个写日志，并且将写命令放入缓存
            if(!StateUtils.isNowTransaction(globalContext) &&
                    MessageUtils.isWriteRequest(request.getMessageType(), getGlobalContext().getHandlerFactory())){
                // 更新一下内部状态
                long writeId = getGlobalContext().getGlobalState().getWriteCount().incrementAndGet();
                // 为Request和Response设置writeId
                Field field = request.getClass().getDeclaredField("writeId_");
                field.setAccessible(true);
                field.set(request, writeId);
                okBuilder.setWriteId(writeId);
                // 向writeLogExecutor提交一个写日志
                getGlobalContext().getWriteLogExecutor().submit(request);
                // 将写命令放入缓存
                getGlobalContext().getRequestBuffer().add(request);
            }
            doHandle(request, okBuilder);
            okBuilder.setClientTId(request.getClientTId());

            return okBuilder.build();
            // 统一的异常处理
        } catch (CheckNotPassException e) {
            Proto.Response.Builder builder = MessageUtils.builderWithCode(Proto.ResponseCode.CheckNotPass);
            builder.setMessageType(request.getMessageType());
            builder.setClientTId(request.getClientTId());
            String messageTypeStr = MessageUtils.getMessageTypeCamelString(request.getMessageType());
            builder.setField(builder.getDescriptorForType().findFieldByName(messageTypeStr + "Response"), e.getResponse());
            return builder.build();
        } catch (ValueTypeNotMatchException e) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.ValueTypeNotMatch, request.getClientTId());
        } catch (InvalidParamException e) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.InvalidParam, request.getClientTId());
        } catch (Exception e) {
            log.error("MyUnknown Exception: ", e);
            return MessageUtils.responseWithCode(Proto.ResponseCode.Unknown, request.getClientTId());
        }
    }

    public abstract void doHandle(Proto.Request request, Proto.Response.Builder responseBuilder) throws Exception;
}
