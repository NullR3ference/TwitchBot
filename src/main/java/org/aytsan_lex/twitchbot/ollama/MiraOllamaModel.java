package org.aytsan_lex.twitchbot.ollama;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ArrayListMultimap;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;

import org.aytsan_lex.twitchbot.TwitchBot;

public class MiraOllamaModel implements IOllamaModel
{
    private static final Logger LOG = LoggerFactory.getLogger(MiraOllamaModel.class);
    private static final Object API_ACCESS_SYNC = new Object();

    private final OllamaChatRequestBuilder ollamaChatRequestBuilder;
    private final ArrayListMultimap<String, String> modelParams;

    public MiraOllamaModel()
    {
        final String modelName = TwitchBot.getConfigManager().getConfig().getMiraModelName();
        this.ollamaChatRequestBuilder = OllamaChatRequestBuilder.getInstance(modelName);
        this.modelParams = ArrayListMultimap.create();

        try
        {
            final ArrayList<String> params = new ArrayList<>(List.of(
                    TwitchBot.getOllamaModelsManager().getAPI().getModelDetails(modelName)
                            .getParameters().split("\n")
            ));

            params.forEach(p -> {
                final String[] entry = p.split("\\s+");
                this.modelParams.put(entry[0], entry[1]);
            });

            LOG.debug("Model params: {}", this.modelParams);
        }
        catch (Exception e)
        {
            LOG.error("Failed to get model details: {}", e.getMessage());
        }
    }

    @Override
    public String chatWithModel(final ModelMessage message)
    {
        try
        {
            OllamaChatResult chatResult;

            synchronized (API_ACCESS_SYNC)
            {
                LOG.info("Message to model: '{}'", message.formatedMessage());
                chatResult = TwitchBot.getOllamaModelsManager().getAPI().chat(
                        this.ollamaChatRequestBuilder
                                .withMessage(OllamaChatMessageRole.USER, message.formatedMessage())
                                .build()
                );
            }

            LOG.debug(
                    "Response from model took: {} ms",
                    Duration.ofNanos(chatResult.getResponseModel().getTotalDuration()).toMillis()
            );
            return chatResult.getResponseModel().getMessage().getContent();
        }
        catch (Exception e)
        {
            LOG.error("Error: {}", e.getMessage());
            return "";
        }
    }

    @Override
    public ArrayListMultimap<String, String> getParams()
    {
        return this.modelParams;
    }
}
