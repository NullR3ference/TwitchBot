package org.aytsan_lex.twitchbot.botcommands;

public class BotCommandError extends RuntimeException
{
    public BotCommandError(String message)
    {
        super(message);
    }
}
