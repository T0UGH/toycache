package com.t0ugh.server.callback;

import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SendResponseCallback implements Callback {

    @Setter
    private ChannelHandlerContext channelHandlerContext;

    @Override
    public void callback(Proto.Request request, Proto.Response response) {
        channelHandlerContext.writeAndFlush(response);
      log.info(response.toString());
    }
}
