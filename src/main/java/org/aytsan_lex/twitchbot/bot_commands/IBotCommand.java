package org.aytsan_lex.twitchbot.bot_commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public interface IBotCommand
{
    void execute(IRCMessageEvent event, ArrayList<String> args) throws BotCommandError;
    int getRequiredPermissionLevel();
    int getCooldown();
    boolean isMuted();
}
