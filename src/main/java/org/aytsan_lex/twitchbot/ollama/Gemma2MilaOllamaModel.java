package org.aytsan_lex.twitchbot.ollama;

public class Gemma2MilaOllamaModel implements IOllamaModel
{
    // TODO: Implement Gemma2MilaOllamaModel
    // Anti-Mira: Negative and toxic

    @Override
    public boolean checkConnection()
    {
        return false;
    }

    @Override
    public String chatWithModel(final String userName, final String message)
    {
        return "";
    }
}
