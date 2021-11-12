package com.t0ugh.server;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class GlobalState {
    private AtomicInteger updateCount;
}
