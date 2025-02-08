package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;

public class JoinToChatBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);
        final String targetChannelName = args.get(0);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            TwitchBot.instance().joinToChat(targetChannelName);
            TwitchBot.LOGGER.info("Joined to: [{}]", targetChannelName);

            if (!super.isTimedOutOnChannelOrModify(channelName))
            {
                super.replyToMessage(
                        event.getChannel(),
                        event.getTwitchChat(),
                        event.getMessageId().get(),
                        "Подключен к: [%s]".formatted(targetChannelName),
                        BotConfigManager.getConfig().getDelayBetweenMessages()
                );
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
