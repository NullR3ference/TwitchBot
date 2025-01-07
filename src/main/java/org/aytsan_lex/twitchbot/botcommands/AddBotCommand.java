package org.aytsan_lex.twitchbot.botcommands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.IrcChatMessageHandler;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class AddBotCommand extends BotCommandBase
{
    public AddBotCommand()
    {
        super(2);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event) ||
            !(args[1] instanceof String newChannelName) ||
            !(args[2] instanceof IrcChatMessageHandler ircMessageHandler))
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
                    "Недостаточно прав SOSI текущий уровень: %d, требуется: %d".formatted(userPermLevel, this.getRequiredPermissionLevel()),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        if (TwitchBot.instance().channelExists(newChannelName))
        {
            if (BotConfig.instance().addChannel(newChannelName))
            {
                super.replyToMessageWithDelay(
                        channelName,
                        userId,
                        messageId,
                        event.getTwitchChat(),
                        "Канал добавлен: [%s]".formatted(newChannelName),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );

                BotConfig.instance().saveChanges();
                ircMessageHandler.addLogger(newChannelName);
                TwitchBot.instance().joinToChat(newChannelName);
            }
            else
            {
                super.replyToMessageWithDelay(
                        channelName,
                        userId,
                        messageId,
                        event.getTwitchChat(),
                        "Канал уже добавлен: [%s]".formatted(newChannelName),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
            }
        }
        else
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Канал не найден: [%s]".formatted(newChannelName),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
