package org.aytsan_lex.twitchbot.ollama;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;

import org.aytsan_lex.twitchbot.TwitchBot;

public class MiraOllamaModel implements IOllamaModel
{
    private static final int MAX_USER_QUESTIONS_HISTORY = 10;
    private static final Logger LOG = LoggerFactory.getLogger(MiraOllamaModel.class);

    private final OllamaChatRequestBuilder ollamaChatRequestBuilder;
    private final ArrayList<String> userQuestionsHistory = new ArrayList<>(MAX_USER_QUESTIONS_HISTORY);

    public MiraOllamaModel()
    {
        this.ollamaChatRequestBuilder =
                OllamaChatRequestBuilder.getInstance(TwitchBot.getConfigManager().getConfig().getMiraModelName());
    }

    @Override
    public String chatWithModel(final ModelMessage message)
    {
        try
        {
            OllamaChatResult chatResult;
            final Instant start = Instant.now();

            LOG.info("Message to model: '{}'", message.formatedMessage());
            this.putQuestionInHistory(message.userName(), message.originalMessage());

            chatResult = TwitchBot.getOllamaModelsManager().getAPI().chat(
                    this.ollamaChatRequestBuilder
                            .withMessage(OllamaChatMessageRole.USER, message.formatedMessage())
                            .build()
            );

            final Instant finish = Instant.now();
            LOG.info("Response from model took: {} ms", Duration.between(start, finish).toMillis());

            return chatResult.getResponse();
        }
        catch (Exception e)
        {
            LOG.error("Error: {}", e.getMessage());
        }

        return "";
    }

    @Override
    public ArrayList<String> getQuestionsHistory()
    {
        return this.userQuestionsHistory;
    }

    @Override
    public void clearQuestionsHistory()
    {
        this.userQuestionsHistory.clear();
    }

    private void putQuestionInHistory(final String userName, final String question)
    {
        if (this.userQuestionsHistory.size() < MAX_USER_QUESTIONS_HISTORY)
        {
            this.userQuestionsHistory.add("%s: %s".formatted(userName, question));
        }
    }
}
