package com.t0ugh.server.handler;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
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

    private final Map<Proto.MessageType, Handler> m;

    public HandlerFactory(GlobalContext globalContext) {
        m = new HashMap<>();
        registerAll(globalContext);
    }

    public Optional<Handler> getHandler(Proto.MessageType messageType) {
        if (!m.containsKey(messageType))
            return Optional.empty();
        return Optional.of(m.get(messageType));
    }

    public int size() {
        return m.size();
    }

    public void registerAll(GlobalContext globalContext){
        Reflections reflections = new Reflections("com.t0ugh.server.handler");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(HandlerAnnotation.class);
        classSet.forEach(clazz -> {
            try {
                Proto.MessageType messageType = clazz.getAnnotation(HandlerAnnotation.class).messageType();
                Constructor<?> cons = clazz.getConstructor(GlobalContext.class);
                register(messageType, (Handler) cons.newInstance(globalContext));
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
