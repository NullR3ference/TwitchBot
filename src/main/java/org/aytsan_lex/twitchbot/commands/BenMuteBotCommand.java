package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.CommandHandler;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class BenMuteBotCommand extends BotCommandBase
{
    public BenMuteBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof Boolean isMuted) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            BotConfig.instance().setCommandIsMuted(CommandHandler.Commands.BEN.name(), isMuted);
            BotConfig.instance().saveChanges();
            TwitchBot.LOGGER.info("Ben command muted = {}", isMuted);
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
