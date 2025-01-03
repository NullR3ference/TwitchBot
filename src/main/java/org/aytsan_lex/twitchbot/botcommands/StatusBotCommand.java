package org.aytsan_lex.twitchbot.botcommands;

import org.aytsan_lex.twitchbot.BotConfig;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class StatusBotCommand extends BotCommandBase
{
    public StatusBotCommand()
    {
        super(1);
    }

    @Override
    public int execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new IllegalArgumentException("Argument must be instance of IRCMessageEvent!");
        }

        final String userId = event.getUserId();
        final String messageId = event.getMessageId().get();
        final String channelName = event.getChannel().getName();
        final int userPermLevel = BotConfig.instance().getPermissionLevel(userId);

        if (userPermLevel < super.getRequiredPermissionLevel())
        {
            super.replyToMessage(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Недостаточно прав SOSI"
            );
            return 1;
        }

        super.replyToMessage(
                channelName,
                userId,
                messageId,
                event.getTwitchChat(),
                "Каналы: " + BotConfig.instance().getChannels().size()
        );

        return 0;
    }
}
