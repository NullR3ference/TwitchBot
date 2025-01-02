package org.aytsan_lex.twitchbot;

import java.util.ArrayList;

public class TwitchBotLauncher
{
    public static void main(String[] args)
    {
        final String client_id = BotConfig.instance().getClientId();
        final String access_token = BotConfig.instance().getAccessToken();
        final ArrayList<String> channels = BotConfig.instance().getChannels();

        System.out.println("[*] Initializing twitch bot...");
        TwitchBot.instance().init(client_id, access_token).withChannels(channels).start();

        System.out.println("[+] Started");
    }
}
