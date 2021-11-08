package com.t0ugh.server.handler;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.handler.impl.key.ExistsHandler;
import com.t0ugh.server.handler.impl.string.GetHandler;
import com.t0ugh.server.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 一个工厂类: 保存请求与处理器之间的一一对应关系
 * */
@Slf4j
public class HandlerFactory {

    private final Map<Proto.MessageType, Handler> m = new HashMap<>();

    public HandlerFactory(Storage storage) {
        registerAll(storage);
    }

    public Optional<Handler> getHandler(Proto.MessageType messageType) {
        if (!m.containsKey(messageType))
            return Optional.empty();
        return Optional.of(m.get(messageType));
    }

    public int size() {
        return m.size();
    }

    public void registerAll(Storage storage){
        Reflections reflections = new Reflections("com.t0ugh.server.handler");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(HandlerAnnotation.class);
        classSet.forEach(clazz -> {
            try {
                Proto.MessageType messageType = clazz.getAnnotation(HandlerAnnotation.class).type();
                Constructor<?> cons = clazz.getConstructor(Storage.class);
                register(messageType, (Handler) cons.newInstance(storage));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                log.error("handler register failed", e);
            }
        });
    }

    public void register(Proto.MessageType messageType, Handler handler) {
        m.put(messageType, handler);
    }
}
