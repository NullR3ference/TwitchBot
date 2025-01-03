package org.aytsan_lex.twitchbot.botcommands;

public interface IBotCommand
{
    int execute(Object... args);
    int getRequiredPermissionLevel();
}
