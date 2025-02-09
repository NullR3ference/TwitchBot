package org.aytsan_lex.twitchbot;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.aytsan_lex.twitchbot.webui.WebUiServer;

public class TwitchBotLauncher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchBotLauncher.class);
    private static Instant START_TIME = null;

    private static final WebUiServer webUiServer = WebUiServer.builder()
            .withHostAddress("0.0.0.0")
            .withPort(4004)
            .build();

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

        if (BotConfigManager.credentialsIsEmpty())
        {
            LOGGER.error("Empty credentials! clientId and accessToken is required!");
            System.exit(1);
        }

        TwitchBot.initialize(
                BotConfigManager.getConfig().getClientId(),
                BotConfigManager.getConfig().getAccessToken()
        );

        if (!TwitchBot.isInitialized())
        {
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
