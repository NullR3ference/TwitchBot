package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.CommandHandler;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class IqMuteBotCommand extends BotCommandBase
{
    public IqMuteBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof Boolean isMuted) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

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
