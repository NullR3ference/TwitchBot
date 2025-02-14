package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;

public class RemoveChannelBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelName = (args.isEmpty()) ? event.getChannel().getName() : args.get(0).trim();
            final String targetChannelId = chat.getChannelNameToChannelId().get(targetChannelName);

            if (targetChannelId != null)
            {
                if (TwitchBot.channelExists(targetChannelName))
                {
                    if (BotConfigManager.removeChannel(targetChannelName))
                    {
                        TwitchBot.replyToMessage(
                                event.getChannel().getName(),
                                event.getMessageId().get(),
                                "Канал удален: [%s]".formatted(targetChannelName)
                        );

                        TwitchBot.leaveFromChat(targetChannelName);
                        BotConfigManager.removeChannel(targetChannelName);
                        BotConfigManager.saveConfig();

                        TwitchBot.LOGGER.info("Channel removed: [{}]", targetChannelName);
                    }
                }
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
