package org.aytsan_lex.twitchbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class TwitchBotLauncher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchBotLauncher.class);
    private static Instant START_TIME = null;

    public static void main(String[] args)
    {
        BotConfigManager.initialize();
        FiltersManager.initialize();

        BotConfigManager.readConfig();
        FiltersManager.readFilters();

        if (BotConfigManager.credentialsIsEmpty())
        {
            LOGGER.error("Empty credentials! clientId and accessToken is required!");
            System.exit(1);
        }

        OllamaModelsManager.initialize();

        TwitchBot.instance().initialize(
                BotConfigManager.getConfig().getClientId(),
                BotConfigManager.getConfig().getAccessToken()
        );

        TwitchBot.instance().start();
        START_TIME = Instant.now();
    }

    public static Instant getStartTime()
    {
        return START_TIME;
    }
}
