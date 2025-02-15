package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.filters.MiraFilters;

public class FiltersInfoBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            TwitchBot.replyToMessage(
                    event.getChannel().getName(),
                    event.getMessageId().get(),
                    this.createInfoMessage()
            );
        }
        else
        {
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private String createInfoMessage()
    {
        final MiraFilters filters = TwitchBot.getFiltersManager().getMiraFilters();

        return "Pre-filter: %d | Post-filter: %d | MsgLen: %d | WordLen: %d"
                .formatted(
                        filters.getPreFilter().size(),
                        filters.getPostFilter().size(),
                        filters.getMessageLengthFilter(),
                        filters.getWordLengthFilter()
                );
    }
}
