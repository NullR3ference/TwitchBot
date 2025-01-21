package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.CommandHandler;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.TwitchBot;

public class MiraMuteBotCommand extends BotCommandBase
{
    public MiraMuteBotCommand()
    {
        super(4);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof Boolean isMuted) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

//        final String userId = event.getUser().getId();
//        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);
        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfig.instance().getPermissionLevelByName(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            BotConfig.instance().setCommandIsMuted(CommandHandler.Commands.MIRA.name(), isMuted);
            BotConfig.instance().saveChanges();
            TwitchBot.LOGGER.info("Mira command muted = {}", isMuted);
        }
    }
}
