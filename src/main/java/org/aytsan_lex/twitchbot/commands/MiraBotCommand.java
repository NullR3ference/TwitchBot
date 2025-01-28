package org.aytsan_lex.twitchbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.twitch4j.chat.TwitchChat;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.ollama.OllamaModels;
import org.aytsan_lex.twitchbot.filters.MiraPreFilter;
import org.aytsan_lex.twitchbot.filters.MiraPostFilter;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class MiraBotCommand extends BotCommandBase
{
    private static final int MAX_TWITCH_MESSAGE_LEN = 300;

    public MiraBotCommand()
    {
        super(1);
    }

    @Override
    public void execute(Object... args)
    {
        // TODO: Make AI to able read chat in real time

        if (!(args[0] instanceof String message) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final EventChannel channel = event.getChannel();
        final String userId = event.getUser().getId();
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.instance().getPermissionLevel(userName);

        if (!OllamaModels.GEMMA2_MIRA.checkConnection())
        {
            TwitchBot.LOGGER.warn("Ollama connection failed: {}", BotConfigManager.instance().getConfig().getOllamaHost());
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
            super.replyToMessage(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Прости зайка, я пока не могу общаться с тобой, создателю надо выдать разрешение, прежде чем я смогу тебе отвечать (("
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

        final String response = OllamaModels.GEMMA2_MIRA.chatWithModel(userName, message).trim();
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
        for (final Pattern pattern : MiraPreFilter.VALUES)
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
        String filtered = MiraPostFilter.URL_PATTERN.matcher(response).replaceAll("**");

        for (final Pattern pattern : MiraPostFilter.VALUES)
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
        if (response.length() <= MAX_TWITCH_MESSAGE_LEN)
        {
            return response;
        }
        return response.substring(0, MAX_TWITCH_MESSAGE_LEN - 1);
    }
}
