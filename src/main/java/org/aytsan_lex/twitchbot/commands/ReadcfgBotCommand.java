package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfig;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class ReadcfgBotCommand extends BotCommandBase
{
    public ReadcfgBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userId = event.getUser().getId();
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final String channelName = event.getChannel().getName();
//        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);
        final int permissionLevel = BotConfig.instance().getPermissionLevelByName(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            BotConfig.instance().updateConfig();
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Конфиг обновлен",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
