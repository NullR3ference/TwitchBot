package org.aytsan_lex.twitchbot.commands;

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
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final String currentChannelName = event.getChannel().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userName);

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
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
