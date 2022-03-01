package com.t0ugh.server;

import com.t0ugh.server.config.Configs;
import com.t0ugh.server.db.DBExecutor;
import com.t0ugh.server.executor.MemoryOperationExecutor;
import com.t0ugh.server.executor.MessageExecutor;
import com.t0ugh.server.handler.HandlerFactory;
import com.t0ugh.server.rollbacker.RollBackerFactory;
import com.t0ugh.server.storage.MemoryStorage;
import com.t0ugh.server.storage.Storage;
import com.t0ugh.server.tick.DeleteKeyTicker;
import com.t0ugh.server.tick.TickDriverImpl;
import com.t0ugh.server.utils.WriteLogUtils;
import com.t0ugh.server.writeLog.WriteLogExecutor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        new Bootstrap().startDev();
    }
}
