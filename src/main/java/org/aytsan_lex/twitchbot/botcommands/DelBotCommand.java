package org.aytsan_lex.twitchbot.botcommands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.IrcChatMessageHandler;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class DelBotCommand extends BotCommandBase
{
    public DelBotCommand()
    {
        super(2);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event) ||
            !(args[1] instanceof String targetChannelName) ||
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

        if (BotConfig.instance().removeChannel(targetChannelName))
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Канал удален: [%s]".formatted(targetChannelName),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );

            TwitchBot.instance().leaveFromChat(targetChannelName);
            ircMessageHandler.removeLogger(targetChannelName);
            BotConfig.instance().saveChanges();
        }
        else
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Канал уже удален: [%s]".formatted(targetChannelName),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
