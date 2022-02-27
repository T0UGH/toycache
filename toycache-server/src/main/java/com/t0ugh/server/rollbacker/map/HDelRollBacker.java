package com.t0ugh.server.rollbacker.map;

import com.google.common.collect.Maps;
import com.t0ugh.sdk.exception.ValueTypeNotMatchException;
import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.rollbacker.RollBackerAnnotation;
import com.t0ugh.server.utils.HandlerUtils;
import com.t0ugh.server.utils.MessageUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * HDel可能会删除多个, 本来就不存在的不用管, 本来存在的需要存储起来以便恢复
 * */
@RollBackerAnnotation(messageType = Proto.MessageType.HDel)
public class HDelRollBacker extends AbstractMapRollBacker {

    private Map<String, String> mapExists = Maps.newHashMap();

    public HDelRollBacker(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    public void doRollBack() throws Exception {
        mapExists.entrySet().forEach(entry -> {
            try {
                getGlobalContext().getStorage().hSet(getKey(), entry.getKey(), entry.getValue());
            } catch (ValueTypeNotMatchException ignored) {}
        });
    }

    @Override
    public void doBeforeHandle(Proto.Request request) throws Exception {
        Proto.HDelRequest hDelRequest = request.getHDelRequest();
        List<String> fieldsNeedDel = hDelRequest.getFieldsList();
        MessageUtils.assertCollectionNotEmpty(fieldsNeedDel);
        MessageUtils.assertAllStringNotNullOrEmpty(fieldsNeedDel);
        for (String field: fieldsNeedDel) {
            Optional<String> op = getGlobalContext().getStorage().hGet(getKey(), field);
            op.ifPresent(s -> mapExists.put(field, s));
        }
    }
}
