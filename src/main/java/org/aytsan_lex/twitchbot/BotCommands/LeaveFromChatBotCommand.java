package org.aytsan_lex.twitchbot.BotCommands;

import com.github.twitch4j.chat.TwitchChat;
import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class LeaveFromChatBotCommand extends BotCommandBase
{
    public LeaveFromChatBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String targetChannelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final String currentChannelName = event.getChannel().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelId = chat.getChannelNameToChannelId().get(targetChannelName);
            if ((targetChannelId != null) && !BotConfig.instance().isOwner(targetChannelId))
            {
                super.replyToMessageWithDelay(
                        currentChannelName,
                        userId,
                        messageId,
                        chat,
                        "Отключен от: [%s]".formatted(targetChannelName),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
                TwitchBot.instance().leaveFromChat(targetChannelName);
            }
        }
        else
        {
            super.replyToMessageWithDelay(
                    currentChannelName,
                    userId,
                    messageId,
                    chat,
                    "Требуется %d+ уровень доступа, у тебя: %d SOSI"
                            .formatted(this.getRequiredPermissionLevel(), permissionLevel),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
