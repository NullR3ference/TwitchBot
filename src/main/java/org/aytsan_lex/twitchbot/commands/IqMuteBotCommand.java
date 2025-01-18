package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.CommandHandler;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class IqMuteBotCommand extends BotCommandBase
{
    public IqMuteBotCommand()
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

        final String userId = event.getUser().getId();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            BotConfig.instance().setCommandIsMuted(CommandHandler.Commands.IQ.name(), isMuted);
            BotConfig.instance().saveChanges();
            TwitchBot.LOGGER.info("IQ command muted = {}", isMuted);
        }
    }
}
