package org.aytsan_lex.twitchbot.ui_commands;

public class UiCommandError extends RuntimeException
{
    public UiCommandError(String message)
    {
        super(message);
    }
}
