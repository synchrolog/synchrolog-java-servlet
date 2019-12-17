package com.synchrolog.logger;

import java.util.HashMap;
import java.util.Map;

public class LoggerContextManager {

    private static ThreadLocal<LoggerContext> loggerContext = new ThreadLocal<>();

    public static void set(LoggerContext context) {
        loggerContext.set(context);
    }

    public static LoggerContext get() {
        return loggerContext.get();
    }


}
