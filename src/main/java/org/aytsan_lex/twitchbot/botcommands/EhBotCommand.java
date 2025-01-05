package org.aytsan_lex.twitchbot.botcommands;

import org.aytsan_lex.twitchbot.BotConfig;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class EhBotCommand extends BotCommandBase
{
    public EhBotCommand()
    {
        super(1);
    }

    @Override
    public int execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String channelName = event.getChannel().getName();
        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final int userPermLevel = BotConfig.instance().getPermissionLevel(userId);

        if (userPermLevel < this.getRequiredPermissionLevel())
        {
            super.replyToMessage(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Недостаточно прав SOSI текущий уровень: %d, требуется: %d".formatted(userPermLevel, this.getRequiredPermissionLevel())
            );
            return 1;
        }

        super.sendMessage(
                channelName,
                userId,
                event.getTwitchChat(),
                "eh девочку бы"
        );

        return 0;
    }
}
