package org.aytsan_lex.twitchbot;

import java.util.ArrayList;

public class TwitchBotLauncher
{
    public static void main(String[] args)
    {
        System.out.println("[=] BotConfig.CONFIG_PATH: " + BotConfig.CONFIG_PATH);
        System.out.println("[=] BotConfig.LOG_BASE_PATH: " + BotConfig.LOG_BASE_PATH);

        final String clientId = BotConfig.instance().getClientId();
        final String accessToken = BotConfig.instance().getAccessToken();
        final String refreshToken = BotConfig.instance().getRefreshToken();
        final ArrayList<String> tokenScopes = BotConfig.instance().getTokenScopes();

        System.out.println(clientId);
        System.out.println(accessToken);
        System.out.println(refreshToken);
        System.out.println(tokenScopes);

        System.out.println("[*] Initializing twitch bot...");
        TwitchBot.instance().init(clientId, accessToken, refreshToken, tokenScopes).start();

        System.out.println("[+] Started");
    }
}
