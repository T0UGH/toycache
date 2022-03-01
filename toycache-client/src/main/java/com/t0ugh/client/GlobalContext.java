package com.t0ugh.client;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalContext {
    private Channel channel;
    private Dispatcher dispatcher;
}
