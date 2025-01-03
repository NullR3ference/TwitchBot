package org.aytsan_lex.twitchbot;

public class TwitchBotLauncher
{
    public static void main(String[] args)
    {
        System.out.println("[=] BotConfig.CONFIG_PATH: " + BotConfig.CONFIG_PATH);
        System.out.println("[=] BotConfig.LOG_BASE_PATH: " + BotConfig.LOG_BASE_PATH);

        final String client_id = BotConfig.instance().getClientId();
        final String access_token = BotConfig.instance().getAccessToken();

        System.out.println("[*] Initializing twitch bot...");
        TwitchBot.instance().init(client_id, access_token).start();

        System.out.println("[+] Started");
    }
}
