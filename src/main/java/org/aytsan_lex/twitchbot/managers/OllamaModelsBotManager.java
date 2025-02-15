package org.aytsan_lex.twitchbot.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ollama4j.OllamaAPI;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.ollama.IOllamaModel;
import org.aytsan_lex.twitchbot.ollama.MiraOllamaModel;

public class OllamaModelsBotManager implements IBotManager
{
    private static final Logger LOG = LoggerFactory.getLogger(OllamaModelsBotManager.class);
    private static final int REQUEST_TIMEOUT = 600;

    private static OllamaAPI API = null;
    private static IOllamaModel miraModel = new MiraOllamaModel();

    public OllamaAPI getAPI()
    {
        return API;
    }

    public boolean checkConnection()
    {
        boolean result = false;
        try { result = API.ping(); }
        catch (RuntimeException ignored) {  }
        return result;
    }

    public IOllamaModel getMiraModel()
    {
        return miraModel;
    }

    @Override
    public boolean initialize()
    {
        LOG.info("Initializing...");

        API = new OllamaAPI(TwitchBot.getConfigManager().getConfig().getOllamaHost());
        API.setRequestTimeoutSeconds(REQUEST_TIMEOUT);
        API.setVerbose(false);

        if (!checkConnection())
        {
            LOG.warn("Failed to connect Ollama on: {}", TwitchBot.getConfigManager().getConfig().getOllamaHost());
        }

        return true;
    }

    @Override
    public void shutdown()
    {
        LOG.info("Shutting down...");

        miraModel.clearQuestionsHistory();
        miraModel = null;

        API = null;
    }
}
