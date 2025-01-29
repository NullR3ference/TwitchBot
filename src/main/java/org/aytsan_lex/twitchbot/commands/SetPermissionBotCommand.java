package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
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

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
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
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
