package com.synchrolog.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class SynchrologLogbackAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        final LoggerContext loggerContext = LoggerContextManager.get();
        if(event.getLevel() == Level.ERROR) {
            System.out.println();
        }
        loggerContext.client.sendLog(loggerContext.params, event.getFormattedMessage());
    }
}
