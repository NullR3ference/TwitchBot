package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class RestartBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            // Assumes that user runs this code via JVM and not over Gradle
            // Because Gradle returns 1
            System.exit(10);
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
