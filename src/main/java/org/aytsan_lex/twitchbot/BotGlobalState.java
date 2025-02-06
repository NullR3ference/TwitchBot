package org.aytsan_lex.twitchbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotGlobalState
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BotGlobalState.class);
    private static boolean miraCommandRunning = false;

    public static synchronized boolean isMiraCommandRunning()
    {
        return miraCommandRunning;
    }

    public static synchronized void setMiraCommandRunning(boolean value)
    {
        miraCommandRunning = value;
    }
}
