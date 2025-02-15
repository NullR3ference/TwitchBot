package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

public class RemoveChannelBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelName = (args.isEmpty()) ? event.getChannel().getName() : args.get(0).trim();
            final String targetChannelId = event.getTwitchChat().getChannelNameToChannelId().get(targetChannelName);

            if (targetChannelId != null)
            {
                if (TwitchBot.channelExists(targetChannelName))
                {
                    if (TwitchBot.getConfigManager().removeChannel(targetChannelName))
                    {
                        TwitchBot.replyToMessage(
                                event.getChannel().getName(),
                                event.getMessageId().get(),
                                "Канал удален: [%s]".formatted(targetChannelName)
                        );

                        TwitchBot.leaveFromChat(targetChannelName);
                        TwitchBot.getConfigManager().removeChannel(targetChannelName);
                        TwitchBot.getConfigManager().saveFile();

                        TwitchBot.LOG.info("Channel removed: [{}]", targetChannelName);
                    }
                }
            }
        }
        else
        {
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
