package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.FiltersManager;
import org.aytsan_lex.twitchbot.filters.MiraFilters;

public class FiltersInfoBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
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
                    BotConfigManager.getConfig().getDelayBetweenMessages()
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
