package org.aytsan_lex.twitchbot.commands;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.FiltersManager;
import org.aytsan_lex.twitchbot.TwitchBot;

public class FiltersInfoBotCommand extends BotCommandBase
{
    public FiltersInfoBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            super.replyToMessageWithDelay(
                    event.getChannel(),
                    event.getUser().getId(),
                    event.getMessageId().get(),
                    event.getTwitchChat(),
                    this.createInfoMessage(),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private String createInfoMessage()
    {
        final int preFilterLinesCount = FiltersManager.getMiraFilters().getPreFilter().size();
        final int postFilterLinesCount = FiltersManager.getMiraFilters().getPostFilter().size();
        final int lenFilter = FiltersManager.getMiraFilters().getMessageLengthFilter();
        return "Pre-filter: %d строк | Post-filter: %d строк | Len-filter: %d".formatted(preFilterLinesCount, postFilterLinesCount, lenFilter);
    }
}
