package org.aytsan_lex.twitchbot.logback_appenders;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import org.aytsan_lex.twitchbot.TwitchBot;

public class WebSocketAppender extends AppenderBase<ILoggingEvent>
{
    private PatternLayoutEncoder encoder;

    @Override
    public void start()
    {
        if (this.encoder == null)
        {
            super.addError("No encoder set for the appender named [%s]".formatted(this.getName()));
            return;
        }

        this.encoder.start();
        super.start();
    }

    @Override
    public void append(final ILoggingEvent loggingEvent)
    {
        if (TwitchBot.getWsUiServer().clientIsConnected())
        {
            final byte[] encodedData = this.encoder.encode(loggingEvent);
            TwitchBot.getWsUiServer().sendBytes(encodedData);
        }
    }

    public PatternLayoutEncoder getEncoder()
    {
        return this.encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder)
    {
        this.encoder = encoder;
    }
}
