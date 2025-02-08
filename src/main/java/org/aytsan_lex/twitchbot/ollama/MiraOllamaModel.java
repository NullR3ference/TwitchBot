package org.aytsan_lex.twitchbot.ollama;

import java.time.Instant;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.OllamaModelsManager;

public class MiraOllamaModel implements IOllamaModel
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MiraOllamaModel.class);
    private static final Object chatSync = new Object();
    private final OllamaChatRequestBuilder ollamaChatRequestBuilder;

    public MiraOllamaModel()
    {
        this.ollamaChatRequestBuilder =
                OllamaChatRequestBuilder.getInstance(BotConfigManager.getConfig().getMiraModelName());
    }

    @Override
    public String chatWithModel(final String message)
    {
        try
        {
            OllamaChatResult chatResult;
            final Instant start = Instant.now();

            synchronized (chatSync)
            {
                LOGGER.info("Sending message to model: '{}'", message);
                chatResult = OllamaModelsManager.getAPI().chat(
                        this.ollamaChatRequestBuilder
                                .withMessage(OllamaChatMessageRole.USER, message)
                                .build()
                );
            }

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
