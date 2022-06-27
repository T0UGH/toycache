package com.t0ugh.server.handler.impl.replicate;

import com.t0ugh.sdk.proto.Proto;
import com.t0ugh.sdk.proto.ZKProto;
import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.HandlerType;
import com.t0ugh.server.enums.MasterState;
import com.t0ugh.server.handler.HandlerAnnotation;
import com.t0ugh.server.handler.impl.AbstractGenericsHandler;
import com.t0ugh.server.utils.ZKUtils;

import java.util.Objects;

@HandlerAnnotation(messageType = Proto.MessageType.FollowerOf, checkExpire = false, handlerType= HandlerType.Other)
public class FollowerOfHandler extends AbstractGenericsHandler<Proto.FollowerOfRequest, Proto.FollowerOfResponse>  {

    public FollowerOfHandler(GlobalContext globalContext) {
        super(globalContext);
    }

    @Override
    protected Proto.FollowerOfResponse doHandle(Proto.FollowerOfRequest followerOfRequest) throws Exception {
        // 如果本来就相同,本来就是follower，那就是follower
        if (Objects.equals(followerOfRequest.getGroupId(), getGlobalContext().getGlobalState().getGroupId())
                && Objects.equals(getGlobalContext().getGlobalState().getMasterState(), MasterState.Follower)){
            return Proto.FollowerOfResponse.newBuilder().setOk(true).build();
        }
        // 需要计算出deletePath
        long originGroupId = getGlobalContext().getGlobalState().getGroupId();
        String deletePath = "";
        if (Objects.equals(getGlobalContext().getGlobalState().getMasterState(), MasterState.Follower)){
            deletePath = ZKUtils.getFollowerPath(originGroupId, getGlobalContext().getGlobalState().getServerId());
        } else {
            deletePath = ZKUtils.getMasterPath(originGroupId);
        }
        // 还需要计算出CreatePath
        String createPath = ZKUtils.getFollowerPath(followerOfRequest.getGroupId(), getGlobalContext().getGlobalState().getServerId());
        // 还需要设置一下ServerMeta
        ZKProto.ServerMeta serverMeta = ZKProto.ServerMeta.newBuilder()
                .setGroupId(followerOfRequest.getGroupId())
                .setServerId(getGlobalContext().getGlobalState().getServerId())
                .setEpoch(0L)
                .setLastWriteId(0L)
                .setServerIp(getGlobalContext().getConfig().getNettyServerIp())
                .setServerPort(getGlobalContext().getConfig().getNettyServerPort())
                .build();
        getGlobalContext().getCreateFollowerToZKExecutor().submit(Proto.Request.newBuilder()
                        .setMessageType(Proto.MessageType.InnerCreateFollowerToZK)
                        .setInnerCreateFollowerToZKRequest(Proto.InnerCreateFollowerToZKRequest.newBuilder()
                                .setCreatePath(createPath)
                                .setDeletePath(deletePath)
                                .setServerMeta(serverMeta)
                                .build())
                .build());
        return Proto.FollowerOfResponse.newBuilder().setOk(true).build();
    }
}
