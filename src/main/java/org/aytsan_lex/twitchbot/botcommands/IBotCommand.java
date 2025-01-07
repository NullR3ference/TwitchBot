package org.aytsan_lex.twitchbot.botcommands;

public interface IBotCommand
{
    void execute(Object... args);
    int getRequiredPermissionLevel();
}
