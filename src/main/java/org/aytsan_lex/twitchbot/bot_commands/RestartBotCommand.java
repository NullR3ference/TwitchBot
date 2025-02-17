package org.aytsan_lex.twitchbot.bot_commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;
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
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

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
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
