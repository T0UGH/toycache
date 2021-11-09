package com.t0ugh.server.handler.impl;

import com.t0ugh.sdk.exception.InvalidParamException;
import com.t0ugh.sdk.exception.KeyExpireException;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.handler.Handler;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * 抽象的处理器
 * 这是一个TemplateMethod模式
 * 此外, 为了注册handler，这个类的所有子类都必须带有HandlerAnnotation注解 {@link com.t0ugh.server.handler.HandlerAnnotation()}
 * */
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
            // 然后要检查是否超时, 超时就删并返回一个超时响应
            Optional<String> o = MessageUtils.getKeyFromRequest(request);
            if (o.isPresent()) {
                boolean expired = globalContext.getExpireMap().deleteIfExpired(o.get());
                if (expired) {
                    globalContext.getStorage().del(o.get());
                    throw new KeyExpireException();
                }
            }
            // 然后调用抽象方法来做实际的处理
            Proto.Response.Builder okBuilder = MessageUtils.okBuilder();
            doHandle(request, okBuilder);
            return okBuilder.build();
        // 统一的异常处理
        } catch (ValueTypeNotMatchException e) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.ValueTypeNotMatch);
        } catch (InvalidParamException e) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.InvalidParam);
        } catch (KeyExpireException e) {
            return MessageUtils.responseWithCode(Proto.ResponseCode.KeyExpired);
        } catch (Exception e) {
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
