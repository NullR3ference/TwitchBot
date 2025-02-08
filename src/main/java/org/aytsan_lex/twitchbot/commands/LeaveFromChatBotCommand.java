package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;

public class LeaveFromChatBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelName = (args.isEmpty()) ? event.getChannel().getName() : args.get(0);
            final String targetChannelId = chat.getChannelNameToChannelId().get(targetChannelName);

            if (!BotConfigManager.getConfig().getRunningOnChannelId().equals(targetChannelId))
            {
                if (!super.isTimedOutOnChannelOrModify(channelName))
                {
                    super.replyToMessage(
                            event.getChannel(),
                            chat,
                            event.getMessageId().get(),
                            "Отключен от: [%s]".formatted(targetChannelName),
                            BotConfigManager.getConfig().getDelayBetweenMessages()
                    );
                }
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
