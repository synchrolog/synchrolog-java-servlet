package com.synchrolog.logger;


import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "SynchrologAppender",
        category = "Core",
        elementType = "appender")
public class SynchrologLog4j2Appender extends AbstractAppender {

    protected SynchrologLog4j2Appender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static SynchrologLog4j2Appender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute(value="ignoreExceptions", defaultBoolean = true) boolean ignoreExceptions,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout) {

        return new SynchrologLog4j2Appender(name, filter, layout != null ? layout : PatternLayout.createDefaultLayout(), ignoreExceptions);
    }

    /*@PluginBuilderFactory
    public static <B extends SynchrologLog4j2Appender.Builder<B>> B newBuilder() {
        return new SynchrologLog4j2Appender.Builder<B>().asBuilder();
    }

    public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<SynchrologLog4j2Appender> {

        @Override
        public SynchrologLog4j2Appender build() {
            return new SynchrologLog4j2Appender(getName(),
                    getFilter(),
                    getOrCreateLayout(),
                    isIgnoreExceptions(),
                    getPropertyArray());
        }
    }*/

    @Override
    public void append(LogEvent event) {
        final LoggerContext loggerContext = LoggerContextManager.get();
        loggerContext.client.sendLog(loggerContext.params, event.getMessage().getFormattedMessage());
    }
}
