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
    public void execute(Object... args)
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
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Недостаточно прав SOSI текущий уровень: %d, требуется: %d"
                            .formatted(userPermLevel, this.getRequiredPermissionLevel()),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        if (BotConfig.instance().getChannels().contains(channelName))
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Сообщений в текущем логе: "
                            + ircMessageHandler.getLogger(channelName).getCurrentLines(),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
        else
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Канал отключен от логирования: [%s]".formatted(channelName),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
