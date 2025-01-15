package org.aytsan_lex.twitchbot.commands;

public class BotCommandError extends RuntimeException
{
    public BotCommandError(String message)
    {
        super(message);
    }
}
