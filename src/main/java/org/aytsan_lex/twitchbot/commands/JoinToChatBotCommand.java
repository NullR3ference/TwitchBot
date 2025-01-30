package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class JoinToChatBotCommand extends BotCommandBase
{
    public JoinToChatBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String channelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            TwitchBot.instance().joinToChat(channelName);
            super.replyToMessageWithDelay(
                    event.getChannel(),
                    event.getUser().getId(),
                    event.getMessageId().get(),
                    event.getTwitchChat(),
                    "Подключен к: [%s]".formatted(channelName),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            TwitchBot.LOGGER.info("Joined to: [{}]", channelName);
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
