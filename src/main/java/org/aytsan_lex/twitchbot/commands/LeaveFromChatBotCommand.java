package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

public class LeaveFromChatBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String channelName = event.getChannel().getName();

            final String targetChannelName = (args.isEmpty()) ? event.getChannel().getName() : args.get(0);
            final String targetChannelId = event.getTwitchChat().getChannelNameToChannelId().get(targetChannelName);

            if (!TwitchBot.getCredentialsManager().getCredentials().userId().equals(targetChannelId))
            {
                TwitchBot.replyToMessage(
                        channelName,
                        event.getMessageId().get(),
                        "Отключен от: [%s]".formatted(targetChannelName)
                );
                TwitchBot.leaveFromChat(targetChannelName);
                TwitchBot.LOG.info("Leaved from: [{}]", targetChannelName);
            }
        }
        else
        {
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
