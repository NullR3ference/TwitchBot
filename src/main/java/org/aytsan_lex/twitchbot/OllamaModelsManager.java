package org.aytsan_lex.twitchbot;

import org.aytsan_lex.twitchbot.ollama.IOllamaModel;
import org.aytsan_lex.twitchbot.ollama.Gemma2MiraOllamaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ollama4j.OllamaAPI;

public class OllamaModelsManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OllamaModelsManager.class);
    private static final int REQUEST_TIMEOUT = 240;
    private static OllamaAPI API = null;

    private static final IOllamaModel miraModel = new Gemma2MiraOllamaModel();

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
