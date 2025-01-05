package org.aytsan_lex.twitchbot.botcommands;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.IrcChatMessageHandler;

public class LinesBotCommand extends BotCommandBase
{
    public LinesBotCommand()
    {
        super(1);
    }

    @Override
    public int execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event) ||
            !(args[1] instanceof IrcChatMessageHandler ircMessageHandler))
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

        if (BotConfig.instance().getChannels().contains(channelName))
        {
            super.replyToMessage(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Сообщений в текущем логе: "
                            + ircMessageHandler.getLogger(channelName).getCurrentLines()
            );
        }
        else
        {
            super.replyToMessage(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Канал отключен от логирования: [%s]".formatted(channelName)
            );
        }

        return 0;
    }
}
