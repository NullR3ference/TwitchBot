package org.aytsan_lex.twitchbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ollama4j.OllamaAPI;

import org.aytsan_lex.twitchbot.ollama.IOllamaModel;
import org.aytsan_lex.twitchbot.ollama.MiraOllamaModel;

public class OllamaModelsManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OllamaModelsManager.class);
    private static final int REQUEST_TIMEOUT = 600;

    private static OllamaAPI API = null;
    private static IOllamaModel miraModel = null;

    public static void initialize()
    {
        LOGGER.info("Initializing...");

        API = new OllamaAPI(BotConfigManager.getConfig().getOllamaHost());
        API.setRequestTimeoutSeconds(REQUEST_TIMEOUT);
        API.setVerbose(false);

        if (!checkConnection())
        {
            LOGGER.warn("Failed to connect Ollama on: {}", BotConfigManager.getConfig().getOllamaHost());
        }

        miraModel = new MiraOllamaModel();
    }

    public static OllamaAPI getAPI()
    {
        return API;
    }

    public static boolean checkConnection()
    {
        boolean result = false;
        try { result = API.ping(); }
        catch (RuntimeException ignored) {  }
        return result;
    }

    public static IOllamaModel getMiraModel()
    {
        return miraModel;
    }
}
