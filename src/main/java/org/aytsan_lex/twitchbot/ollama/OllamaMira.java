package org.aytsan_lex.twitchbot.ollama;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

public class OllamaMira
{
    private final static int REQUEST_TIMEOUT_IN_SECONDS = 60;

    private OllamaAPI ollamaAPI;
    private OllamaChatRequestBuilder ollamaChatRequestBuilder;
    private OllamaChatResult chatResult;

    private static OllamaMira instance = null;

    private OllamaMira()
    {
        try
        {
            this.ollamaAPI = new OllamaAPI("http://localhost:11434");
            this.ollamaAPI.setRequestTimeoutSeconds(REQUEST_TIMEOUT_IN_SECONDS);

            this.ollamaChatRequestBuilder = OllamaChatRequestBuilder.getInstance("gemma2-mira");
            this.chatResult = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String question(final String userName, final String message)
    {
        try
        {
            final String finalMessage = "'%s' говорит: %s".formatted(userName, message);

            if (this.chatResult == null)
            {
                this.chatResult = ollamaAPI.chat(
                        this.ollamaChatRequestBuilder.withMessage(OllamaChatMessageRole.USER, finalMessage).build()
                );
            }
            else
            {
                this.chatResult = ollamaAPI.chat(
                        this.ollamaChatRequestBuilder
                                .withMessages(this.chatResult.getChatHistory())
                                .withMessage(OllamaChatMessageRole.USER, finalMessage)
                                .build()
                );
            }

            return this.chatResult.getResponse();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static OllamaMira instance()
    {
        if (instance == null) { instance = new OllamaMira(); }
        return instance;
    }
}
