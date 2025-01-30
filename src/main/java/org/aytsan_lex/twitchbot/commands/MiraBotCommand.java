package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.OllamaModelsManager;
import org.aytsan_lex.twitchbot.FiltersManager;
import org.aytsan_lex.twitchbot.BotGlobalState;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class MiraBotCommand extends BotCommandBase
{
    public MiraBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String message) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final EventChannel channel = event.getChannel();
        final String userId = event.getUser().getId();
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (!OllamaModelsManager.checkConnection())
        {
            TwitchBot.LOGGER.warn("Ollama connection failed: {}", BotConfigManager.getConfig().getOllamaHost());
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Ошибка сети. Возможно я отключена. Прости зайка <3",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        if (permissionLevel < this.getRequiredPermissionLevel())
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Прости зайка, я пока не могу общаться с тобой, создателю надо выдать разрешение, прежде чем я смогу тебе отвечать ((",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        if (BotGlobalState.votingIsActive())
        {
            TwitchBot.LOGGER.warn("Cannot interact with Mira while voting is active");
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Прости зайка, я не могу отвечать, пока голосование активно ((",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        if (!miraPreFilter(message))
        {
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Даже не пытайся ))",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        final String response = OllamaModelsManager.getMiraModel()
                .chatWithModel(userName, message)
                .trim();

        final String filteredResponse = truncateResponseLength(miraPostFilter(response));

        TwitchBot.LOGGER.info("Raw model response: {}", response);
        TwitchBot.LOGGER.info("Filtered model response: {}", filteredResponse);

        this.replyToMessage(
                channel,
                userId,
                messageId,
                chat,
                filteredResponse
        );
    }

    private boolean miraPreFilter(final String messageText)
    {
        final ArrayList<Pattern> filters = FiltersManager.getMiraFilters().getPreFilter();

        for (final Pattern pattern : filters)
        {
            if (pattern.matcher(messageText).find())
            {
                TwitchBot.LOGGER.warn("Mira pre-filter failed: {}", messageText);
                return false;
            }
        }

        return true;
    }

    private String miraPostFilter(final String response)
    {
        String filtered = response;
        final ArrayList<Pattern> filters = FiltersManager.getMiraFilters().getPostFilter();

        for (final Pattern pattern : filters)
        {
            final Matcher matcher = pattern.matcher(filtered);
            if (matcher.find())
            {
                filtered = matcher.replaceAll("**");
                TwitchBot.LOGGER.warn("Mira post-filter triggered: '{}'", matcher.pattern());
            }
        }

        return filtered;
    }

    private String truncateResponseLength(final String response)
    {
        final int len = FiltersManager.getMiraFilters().getMessageLengthFilter();
        if (response.length() <= len) { return response; }
        return response.substring(0, len - 4).concat("...");
    }
}
