package org.aytsan_lex.twitchbot.ollama;

import java.time.Duration;
import java.time.Instant;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;

public class Gemma2MiraOllamaModel implements IOllamaModel
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Gemma2MiraOllamaModel.class);
    private final OllamaChatRequestBuilder ollamaChatRequestBuilder;

    public Gemma2MiraOllamaModel()
    {
        this.ollamaChatRequestBuilder = OllamaChatRequestBuilder.getInstance(BotConfigManager.instance().getConfig().getMiraModelName());
    }

    public boolean checkConnection()
    {
        boolean result = false;
        try { result = OllamaModels.API.ping(); }
        catch (RuntimeException ignored) {  }
        return result;
    }

    public String chatWithModel(final String userName, final String message)
    {
        try
        {
            final String finalMessage = "'%s' говорит: %s".formatted(userName, message);
            LOGGER.info("Sending message to model: '{}'", finalMessage);

            final Instant start = Instant.now();
            final OllamaChatResult chatResult = OllamaModels.API.chat(
                    this.ollamaChatRequestBuilder
                            .withMessage(OllamaChatMessageRole.USER, finalMessage)
                            .build()
            );
            final Instant finish = Instant.now();

            LOGGER.info("Response from model, took: {} ms", Duration.between(start, finish).toMillis());
            return chatResult.getResponse();
        }
        catch (Exception e)
        {
            LOGGER.error("Error: {}", e.getMessage());
        }

        return "";
    }
}
