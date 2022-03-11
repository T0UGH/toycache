package com.t0ugh.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.t0ugh.client.Exception.CheckNotPassException;
import com.t0ugh.client.Exception.InvalidParamException;
import com.t0ugh.client.Exception.RequestTimeoutException;
import com.t0ugh.client.Exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;
import lombok.Builder;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// todo: 线程安全性
public class Dispatcher {

    private Map<String, BlockingQueue<Proto.Response>> responseMap;
    private Map<String, List<Callback>> callbackMap;
    private Map<String, Proto.Request> requestMap;
    private AtomicInteger requestCounter;
    private GlobalContext context;

    public Dispatcher(GlobalContext context){
        requestCounter = new AtomicInteger(0);
        responseMap = Maps.newConcurrentMap();
        callbackMap = Maps.newConcurrentMap();
        requestMap = Maps.newConcurrentMap();
        this.context = context;
    }

    public void dispatch(Proto.Response response){
        BlockingQueue<Proto.Response> queue = responseMap.get(response.getClientTId());
        if (!Objects.isNull(queue)){
            responseMap.remove(response.getClientTId());
            queue.offer(response);
        }
        List<Callback> callbacks = callbackMap.get(response.getClientTId());
        if (!Objects.isNull(callbacks) && !callbacks.isEmpty()){
            callbacks.forEach(callback -> {
                callback.callback(requestMap.get(response.getClientTId()), response);
            });
            callbackMap.remove(response.getClientTId());
        }
        requestMap.remove(response.getClientTId());
    }

    public Proto.Response getSync(Proto.Request request) throws InterruptedException {
        //todo: 重试，怎么重试
        BlockingQueue<Proto.Response> queue = responseMap.get(request.getClientTId());
        if (Objects.isNull(queue)){
            throw new RuntimeException();
        }
        Proto.Response response = queue.poll(5, TimeUnit.SECONDS);
        if (Objects.isNull(response)){
            throw new RequestTimeoutException();
        }
        return response;
    }

    public Proto.Response talkSync(Proto.Request request) throws InterruptedException {
        String key = request.getClientTId();
        requestMap.put(key, request);
        responseMap.put(key, new ArrayBlockingQueue<>(1));
        context.getChannel().writeAndFlush(request);
        Proto.Response response = getSync(request);
        assertResponseOK(response);
        return response;
    }

    public void talkAsync(Proto.Request request, List<Callback> callbacks){
        String key = request.getClientTId();
        requestMap.put(key, request);
        callbackMap.put(request.getClientTId(), callbacks);
    }

    private void assertResponseOK(Proto.Response response){
        Proto.ResponseCode responseCode = response.getResponseCode();
        switch (responseCode) {
            case InvalidParam:
                throw new InvalidParamException();
            case CheckNotPass:
                throw new CheckNotPassException();
            case ValueTypeNotMatch:
                throw new ValueTypeNotMatchException();
        }
    }

    public String generateClientTId(){
        return String.valueOf(requestCounter.incrementAndGet());
    }
}
