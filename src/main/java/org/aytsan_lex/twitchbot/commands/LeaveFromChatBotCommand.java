package org.aytsan_lex.twitchbot.commands;

import com.github.twitch4j.chat.TwitchChat;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class LeaveFromChatBotCommand extends BotCommandBase
{
    public LeaveFromChatBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String targetChannelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelId = chat.getChannelNameToChannelId().get(targetChannelName);
            if ((targetChannelId != null) && !BotConfigManager.isOwner(targetChannelId))
            {
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        chat,
                        "Отключен от: [%s]".formatted(targetChannelName),
                        BotConfigManager.getConfig().getDelayBetweenMessages()
                );
                TwitchBot.instance().leaveFromChat(targetChannelName);
                TwitchBot.LOGGER.info("Leaved from: [{}]", targetChannelName);
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
