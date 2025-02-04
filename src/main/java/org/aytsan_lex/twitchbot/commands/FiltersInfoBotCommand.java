package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.FiltersManager;
import org.aytsan_lex.twitchbot.filters.MiraFilters;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

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
        final MiraFilters filters = FiltersManager.getMiraFilters();

        return "Pre-filter: %d | Post-filter: %d | MsgLen: %d | WordLen: %d"
                .formatted(
                        filters.getPreFilter().size(),
                        filters.getPostFilter().size(),
                        filters.getMessageLengthFilter(),
                        filters.getWordLengthFilter()
                );
    }
}
