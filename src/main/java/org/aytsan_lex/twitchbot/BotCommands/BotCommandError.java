package org.aytsan_lex.twitchbot.BotCommands;

public class BotCommandError extends RuntimeException
{
    public BotCommandError(String message)
    {
        super(message);
    }
}
