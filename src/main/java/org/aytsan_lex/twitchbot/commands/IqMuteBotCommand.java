package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.CommandHandler;
import org.aytsan_lex.twitchbot.TwitchBot;

public class IqMuteBotCommand extends BotCommandBase
{
    public IqMuteBotCommand()
    {
        super();
    }

    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);
        final boolean isMuted = Boolean.parseBoolean(args.get(0));

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            BotConfigManager.setCommandIsMuted(CommandHandler.Commands.IQ.name(), isMuted);
            BotConfigManager.writeConfig();
            TwitchBot.LOGGER.info("IQ command muted = {}", isMuted);
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
