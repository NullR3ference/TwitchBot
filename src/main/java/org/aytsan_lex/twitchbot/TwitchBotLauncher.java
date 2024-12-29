package org.aytsan_lex.twitchbot;

import java.util.ArrayList;

public class TwitchBotLauncher
{
    public static void main(String[] args)
    {
        final BotConfig config = BotConfig.instance();

        final String client_id = config.getClientId();
        final String access_token = config.getAccessToken();
        final ArrayList<String> channels = config.getChannels();

        TwitchBot bot = new TwitchBot(client_id, access_token, null)
                .withChannels(channels)
                .start();
    }
}
