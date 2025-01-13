package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.time.Instant;

public class TwitchBotLauncher
{
    private static Instant START_TIME = null;

    public static void main(String[] args)
    {
        final String clientId = BotConfig.instance().getClientId();
        final String clientSecret = BotConfig.instance().getClientSecret();
        final String accessToken = BotConfig.instance().getAccessToken();
        final String refreshToken = BotConfig.instance().getRefreshToken();
        final ArrayList<String> tokenScopes = BotConfig.instance().getTokenScopes();

        TwitchBot.instance().init(clientId, clientSecret, accessToken, refreshToken, tokenScopes);
        TwitchBot.instance().start();

        START_TIME = Instant.now();
    }

    public static Instant getStartTime()
    {
        return START_TIME;
    }
}
