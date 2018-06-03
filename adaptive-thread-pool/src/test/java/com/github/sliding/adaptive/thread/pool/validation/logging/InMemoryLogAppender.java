package com.github.sliding.adaptive.thread.pool.validation.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Plugin(name = "InMemoryLogAppender", category = "Core", elementType = "appender", printObject = true)
public class InMemoryLogAppender extends AbstractAppender {
    private final List<LogEvent> logs = new LinkedList<>();

    /**
     * Constructor that defaults to suppressing exceptions.
     *
     * @param name   The Appender name.
     * @param filter The Filter to associate with the Appender.
     * @param layout The layout to use to format the event.
     */
    public InMemoryLogAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }

    /**
     * Constructor.
     *
     * @param name             The Appender name.
     * @param filter           The Filter to associate with the Appender.
     * @param layout           The layout to use to format the event.
     * @param ignoreExceptions If true, exceptions will be logged and suppressed. If false errors will be logged and
     */
    public InMemoryLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void append(LogEvent event) {
        logs.add(event.toImmutable());
    }

    public void clear() {
        logs.clear();
    }

    public boolean hasLogEvent(String message, Level level) {
        for (LogEvent logEvent : logs) {
            if (logEvent.getLevel().equals(level) && logEvent.getMessage().getFormattedMessage().equals(message))
                return true;
        }
        return false;
    }

    @PluginFactory
    public static InMemoryLogAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new InMemoryLogAppender(name, filter, layout, true);
    }
}
