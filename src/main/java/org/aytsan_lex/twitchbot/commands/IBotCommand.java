package org.aytsan_lex.twitchbot.commands;

public interface IBotCommand
{
    void execute(Object... args);
    int getRequiredPermissionLevel();
    int getCooldown();
}
