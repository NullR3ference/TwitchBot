package org.aytsan_lex.twitchbot.BotCommands;

public interface IBotCommand
{
    void execute(Object... args);
    int getRequiredPermissionLevel();
}
