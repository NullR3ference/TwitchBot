package org.aytsan_lex.twitchbot;

import java.time.Instant;

public class TwitchBotLauncher
{
    private static Instant START_TIME = null;

    public static void main(String[] args)
    {
        TwitchBot.instance().init(
                BotConfigManager.instance().getClientId(),
                BotConfigManager.instance().getAccessToken(),
                BotConfigManager.instance().getRefreshToken()
        );

        TwitchBot.instance().start();
        START_TIME = Instant.now();
    }

    public static Instant getStartTime()
    {
        return START_TIME;
    }
}
