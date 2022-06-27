package com.t0ugh.server.zookeeper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZKState {
    private int voteSize;
}
