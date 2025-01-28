package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.FiltersManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class UpdateFiltersBotCommand extends BotCommandBase
{
    public UpdateFiltersBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.instance().getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            FiltersManager.instance().readFilters();

            super.replyToMessageWithDelay(
                    event.getChannel(),
                    event.getUser().getId(),
                    event.getMessageId().get(),
                    event.getTwitchChat(),
                    "Фильтры обновлены",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
