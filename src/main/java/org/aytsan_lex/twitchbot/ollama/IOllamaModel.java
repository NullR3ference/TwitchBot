package org.aytsan_lex.twitchbot.ollama;

public interface IOllamaModel
{
    String chatWithModel(final String userName, final String message);
}
