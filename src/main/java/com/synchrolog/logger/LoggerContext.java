package com.synchrolog.logger;

import com.synchrolog.client.SynchrologHttpClient;

public class LoggerContext {

    public final LoggerContextParams params;
    public final SynchrologHttpClient client;

    public LoggerContext(LoggerContextParams params, SynchrologHttpClient client) {
        this.params = params;
        this.client = client;
    }
}
