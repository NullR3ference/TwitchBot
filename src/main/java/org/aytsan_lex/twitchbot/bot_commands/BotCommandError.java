package org.aytsan_lex.twitchbot.bot_commands;

public class BotCommandError extends RuntimeException
{
    public BotCommandError(String message)
    {
        super(message);
    }
}
