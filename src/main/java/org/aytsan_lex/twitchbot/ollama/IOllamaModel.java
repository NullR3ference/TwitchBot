package org.aytsan_lex.twitchbot.ollama;

public interface IOllamaModel
{
    boolean checkConnection();
    String chatWithModel(final String userName, final String message);
}
