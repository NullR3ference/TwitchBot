package org.aytsan_lex.twitchbot.bot_commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

public class BenMuteBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final boolean isMuted = Boolean.parseBoolean(args.get(0));
            TwitchBot.getBotCommandsManager().setCommandIsMuted(BenBotCommand.class, isMuted);
            TwitchBot.LOG.info("Ben command muted = {}", isMuted);
        }
        else
        {
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
