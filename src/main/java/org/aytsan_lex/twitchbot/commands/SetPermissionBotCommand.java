package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class SetPermissionBotCommand extends BotCommandBase
{
    public SetPermissionBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String targetUserName) ||
            !(args[1] instanceof Integer targetLevel) ||
            !(args[2] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String channelName = event.getChannel().getName();
        final String userId = event.getUser().getId();
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            BotConfig.instance().setPermissionLevel(targetUserName.toLowerCase(), targetLevel);
            BotConfig.instance().saveChanges();

            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Уровень доступа '%s' -> %d".formatted(targetUserName, targetLevel),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );

            TwitchBot.LOGGER.info("Permission level '{}' -> {}", targetUserName, targetLevel);
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
