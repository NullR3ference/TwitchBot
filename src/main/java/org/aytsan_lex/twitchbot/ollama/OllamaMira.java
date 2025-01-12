package org.aytsan_lex.twitchbot.ollama;

import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

public class OllamaMira
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OllamaMira.class);
    private static final int REQUEST_TIMEOUT_IN_SECONDS = 60;

    private final OllamaAPI ollamaAPI;
    private final OllamaChatRequestBuilder ollamaChatRequestBuilder;

    private static OllamaMira instance = null;

    private OllamaMira()
    {
        this.ollamaAPI = new OllamaAPI("http://localhost:11434");
        this.ollamaAPI.setRequestTimeoutSeconds(REQUEST_TIMEOUT_IN_SECONDS);
        this.ollamaAPI.setVerbose(false);

        this.ollamaChatRequestBuilder = OllamaChatRequestBuilder.getInstance("gemma2-mira");
    }

    public static OllamaMira instance()
    {
        if (instance == null) { instance = new OllamaMira(); }
        return instance;
    }

    public boolean checkConnection()
    {
        boolean result = false;
        try { result = this.ollamaAPI.ping(); }
        catch (RuntimeException ignored) {  }
        return result;
    }

    public String question(final String userName, final String message)
    {
        try
        {
            final String finalMessage = "'%s' говорит: %s".formatted(userName, message);
            LOGGER.info("Sending message to model: '{}'", finalMessage);

            final Instant start = Instant.now();
            final OllamaChatResult chatResult = ollamaAPI.chat(
                    this.ollamaChatRequestBuilder
                            .withMessage(OllamaChatMessageRole.USER, finalMessage)
                            .build()
            );
            final Instant finish = Instant.now();

            LOGGER.info("Response from model, took: {}ms", Duration.between(start, finish).toMillis());
            return chatResult.getResponse();
        }
        catch (Exception e)
        {
            LOGGER.error("Error: {}", e.getMessage());
        }

        return "";
    }
}
