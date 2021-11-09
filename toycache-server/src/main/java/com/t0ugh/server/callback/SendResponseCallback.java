package com.t0ugh.server.callback;

import com.t0ugh.sdk.proto.Proto;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class SendResponseCallback implements Callback{

    @Setter
    private ChannelHandlerContext channelHandlerContext;

    @Override
    public void callback(Proto.Request request, Proto.Response response) throws Exception {
        channelHandlerContext.write(response);
    }
}
