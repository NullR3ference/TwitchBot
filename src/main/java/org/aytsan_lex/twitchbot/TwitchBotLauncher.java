package org.aytsan_lex.twitchbot;

import java.util.ArrayList;

public class TwitchBotLauncher
{
    public static void main(String[] args)
    {
        final String client_id = BotConfig.instance().getClientId();
        final String access_token = BotConfig.instance().getAccessToken();

        System.out.println("[*] Initializing twitch bot...");
        TwitchBot.instance().init(client_id, access_token).start();

        System.out.println("[+] Started");
    }
}
