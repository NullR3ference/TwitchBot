package org.aytsan_lex.twitchbot;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchBotLauncher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchBotLauncher.class);
    private static Instant START_TIME = null;

    public static void main(String[] args)
    {
        initializeOrExitOnFailure();
        TwitchBot.start();
        START_TIME = Instant.now();
    }

    public static Instant getStartTime()
    {
        return START_TIME;
    }

    public static void onRestart()
    {
        LOGGER.info("Restarting....");

        shutdownSystems();
        initializeOrExitOnFailure();
        TwitchBot.start();

        START_TIME = Instant.now();
    }

    private static void initializeOrExitOnFailure()
    {
        BotConfigManager.initialize();
        FiltersManager.initialize();

        BotConfigManager.readConfig();
        FiltersManager.readFilters();

        CommandHandler.initialize();
        OllamaModelsManager.initialize();

        TwitchBot.initialize();

        if (!TwitchBot.isInitialized())
        {
            LOGGER.error("TwitchBot initialization failed");
            System.exit(1);
        }
    }

    private static void shutdownSystems()
    {
        CommandHandler.shutdown();
        TwitchBot.stop();
        TwitchBot.shutdown();
    }
}
