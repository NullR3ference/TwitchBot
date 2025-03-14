package org.aytsan_lex.twitchbot.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ollama4j.OllamaAPI;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.ollama.IOllamaModel;
import org.aytsan_lex.twitchbot.ollama.MiraOllamaModel;

public class OllamaModelsManager implements IManager
{
    private static final Logger LOG = LoggerFactory.getLogger(OllamaModelsManager.class);
    private static final int REQUEST_TIMEOUT = 600;

    private OllamaAPI API = null;
    private IOllamaModel miraModel = null;

    public OllamaAPI getAPI()
    {
        return this.API;
    }

    public boolean checkConnection()
    {
        boolean result = false;
        try { result = this.API.ping(); }
        catch (RuntimeException ignored) {  }
        return result;
    }

    public IOllamaModel getMiraModel()
    {
        return this.miraModel;
    }

    @Override
    public boolean initialize()
    {
        LOG.info("Initializing...");

        this.API = new OllamaAPI(TwitchBot.getConfigManager().getConfig().getOllamaHost());
        this.API.setRequestTimeoutSeconds(REQUEST_TIMEOUT);
        this.API.setVerbose(false);

        if (!checkConnection())
        {
            LOG.warn("Failed to connect Ollama on: {}", TwitchBot.getConfigManager().getConfig().getOllamaHost());
        }

        this.miraModel = new MiraOllamaModel();
        return true;
    }

    @Override
    public void shutdown()
    {
        LOG.info("Shutting down...");
    }
}
