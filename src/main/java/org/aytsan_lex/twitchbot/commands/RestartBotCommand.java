package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.TwitchBotLauncher;

public class RestartBotCommand extends BotCommandBase
{
    private enum SubCommand
    {
        UPDATE
    }

    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            if (!args.isEmpty())
            {
                final String subCommand = args.get(0);

                switch (SubCommand.valueOf(subCommand.toUpperCase()))
                {
                    case UPDATE -> System.exit(10);
                }
            }
            else
            {
                TwitchBotLauncher.onRestart();
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
