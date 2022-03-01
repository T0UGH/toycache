package com.t0ugh.client;

import com.t0ugh.sdk.proto.Proto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToyCache {

    private GlobalContext context;
    private String serverIp;
    private int serverPort;

    public ToyCache(String serverIp, int serverPort){
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        init();
    }

    private void init(){
        context = GlobalContext.builder().build();
        context.setDispatcher(new Dispatcher(context));
        NettyClient nettyClient = new NettyClient(serverIp, serverPort, context);
        Channel channel = nettyClient.connect();
        context.setChannel(channel);
    }

    public boolean exists(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Exists)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setExistsRequest(Proto.ExistsRequest.newBuilder().setKey(key).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getExistsResponse().getExists();
    }

    public boolean del(String key) throws InterruptedException {
        Proto.Request request = Proto.Request.newBuilder()
                .setMessageType(Proto.MessageType.Del)
                .setClientTId(context.getDispatcher().generateClientTId())
                .setDelRequest(Proto.DelRequest.newBuilder().setKey(key).build())
                .build();
        Proto.Response response = context.getDispatcher().talkSync(request);
        return response.getDelResponse().getOk();
    }

}
