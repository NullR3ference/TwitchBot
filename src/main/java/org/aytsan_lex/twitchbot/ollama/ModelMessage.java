package org.aytsan_lex.twitchbot.ollama;

public record ModelMessage(String userName, String originalMessage, String formatedMessage)
{
    public String getUserName()
    {
        return this.userName;
    }

    public String getOriginalMessage()
    {
        return this.originalMessage;
    }

    public String getFormatedMessage()
    {
        return this.formatedMessage;
    }
}
