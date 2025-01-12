package org.aytsan_lex.twitchbot;

import java.util.ArrayList;

public class TwitchBotLauncher
{
    public static void main(String[] args)
    {
        final String clientId = BotConfig.instance().getClientId();
        final String accessToken = BotConfig.instance().getAccessToken();
        final String refreshToken = BotConfig.instance().getRefreshToken();
        final ArrayList<String> tokenScopes = BotConfig.instance().getTokenScopes();

        TwitchBot.instance().init(clientId, accessToken, refreshToken, tokenScopes);
        TwitchBot.instance().start();
    }
}
