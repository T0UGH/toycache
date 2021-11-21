package com.t0ugh.server.rollbacker;

import com.google.common.collect.Maps;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class RollBackerFactory {

    private final Map<Proto.MessageType, Constructor<? extends RollBacker>> m;
    private final GlobalContext globalContext;

    public RollBackerFactory(GlobalContext globalContext) {
        this.m = Maps.newHashMap();
        this.globalContext = globalContext;
    }


    public Optional<RollBacker> getRollBacker(Proto.MessageType messageType) {
        if (!m.containsKey(messageType))
            return Optional.empty();
        try {
            return Optional.of(m.get(messageType).newInstance(globalContext));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }
    }

    public int size() {
        return m.size();
    }

    public void registerAll(){
        Reflections reflections = new Reflections("com.t0ugh.server.rollbacker");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(RollBackerAnnotation.class);
        classSet.forEach(clazz -> {
            try {
                Proto.MessageType messageType = clazz.getAnnotation(RollBackerAnnotation.class).messageType();
                @SuppressWarnings("unchecked")
                Constructor<? extends RollBacker> cons = (Constructor<? extends RollBacker>) clazz.getConstructor(GlobalContext.class);
                register(messageType, cons);
            } catch (NoSuchMethodException e) {
                log.error("handler register failed", e);
            }
        });
    }

    public void register(Proto.MessageType messageType, Constructor<? extends RollBacker> cons) {
        m.put(messageType, cons);
    }
}
