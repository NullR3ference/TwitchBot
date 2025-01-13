package org.aytsan_lex.twitchbot.BotCommands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.CommandHandler;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.TwitchBot;

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

        final String userId = event.getUser().getId();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (BotConfig.instance().isOwner(userId) || (permissionLevel >= super.getRequiredPermissionLevel()))
        {
            BotConfig.instance().setCommandIsMuted(CommandHandler.Commands.BEN.name(), isMuted);
            BotConfig.instance().saveChanges();
            TwitchBot.LOGGER.info("Ben command muted = {}", isMuted);
        }
    }
}
