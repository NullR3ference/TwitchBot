package org.aytsan_lex.twitchbot.BotCommands;

public class BotCommandSharedState
{
    private static boolean benIsMuted = false;
    private static boolean miraIsMuted = false;
    private static boolean iqIsMuted = false;

    public static boolean benCommandIsMuted()
    {
        return benIsMuted;
    }

    public static boolean miraCommandIsMuted()
    {
        return miraIsMuted;
    }

    public static boolean iqCommandIsMuted()
    {
        return iqIsMuted;
    }

    public static synchronized void setBenCommandIsMuted(boolean isMuted)
    {
        benIsMuted = isMuted;
    }

    public static synchronized void setMiraCommandIsMuted(boolean isMuted)
    {
        miraIsMuted = isMuted;
    }

    public static synchronized void setIqCommandIsMuted(boolean isMuted)
    {
        iqIsMuted = isMuted;
    }
}
