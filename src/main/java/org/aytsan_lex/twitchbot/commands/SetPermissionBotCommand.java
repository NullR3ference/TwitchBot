package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class SetPermissionBotCommand extends BotCommandBase
{
    public SetPermissionBotCommand()
    {
        super();
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

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);
        final int targeUserPermissionLevel = BotConfigManager.getPermissionLevel(targetUserName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            if (permissionLevel < targeUserPermissionLevel)
            {
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Нельзя изменить уровень доступа для пользователя с уровнем выше твоего: %d против %d"
                                .formatted(permissionLevel, targeUserPermissionLevel),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );

                TwitchBot.LOGGER.warn(
                        "{}: cannot set permission for '{}' with higher level then you: {} vs {}",
                        userName,
                        targetUserName,
                        permissionLevel,
                        targeUserPermissionLevel
                );
                return;
            }

            if (!targetUserName.toLowerCase().equals(userName))
            {
                BotConfigManager.setPermissionLevel(targetUserName.toLowerCase(), targetLevel);
                BotConfigManager.writeConfig();

                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Уровень доступа '%s' -> %d".formatted(targetUserName, targetLevel),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );

                TwitchBot.LOGGER.info("Permission level '{}' -> {}", targetUserName, targetLevel);
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
