package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;

public class LeaveFromChatBotCommand extends BotCommandBase
{
    public LeaveFromChatBotCommand()
    {
        super();
    }

    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String userName = event.getUser().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);
        final String targetChannelName = args.get(0);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelId = chat.getChannelNameToChannelId().get(targetChannelName);
            if (!BotConfigManager.getConfig().getRunningOnChannelId().equals(targetChannelId))
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
