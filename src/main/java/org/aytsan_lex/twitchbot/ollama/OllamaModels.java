package org.aytsan_lex.twitchbot.ollama;

import io.github.ollama4j.OllamaAPI;
import org.aytsan_lex.twitchbot.BotConfig;

public class OllamaModels
{
    public static final int REQUEST_TIMEOUT = 240;
    public static final OllamaAPI API = new OllamaAPI(BotConfig.instance().getOllamaHost());

    static
    {
        API.setRequestTimeoutSeconds(REQUEST_TIMEOUT);
        API.setVerbose(false);
    }

    public static final IOllamaModel GEMMA2_MIRA = new Gemma2MiraOllamaModel();
    public static final IOllamaModel GEMMA2_MILA = new Gemma2MilaOllamaModel();
}
